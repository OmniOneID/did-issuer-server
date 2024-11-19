/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.AccEcdh;
import org.omnione.did.base.datamodel.data.Candidate;
import org.omnione.did.base.datamodel.data.DidAuth;
import org.omnione.did.base.datamodel.data.EcdhReqData;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.BaseWalletUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.keypair.EcKeyPair;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.did.ProofType;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.service.query.CertificateVcQueryService;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.issuer.v1.api.EnrollFeign;
import org.omnione.did.issuer.v1.api.dto.*;
import org.omnione.did.issuer.v1.dto.EnrollEntityResDto;
import org.springframework.stereotype.Service;

import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;

/**
 * Service for enrolling entity.
 * This class provides methods for enrolling entity.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EnrollEntityServiceImpl implements EnrollEntityService {
    private final EnrollFeign enrollFeign;
    private final FileWalletService walletService;
    private final CertificateVcQueryService certificateVcQueryService;

    /**
     * Enroll entity.
     * This method enrolls an entity with the TAS and returns the result as an EnrollEntityResDto object.
     *
     * @return the result of the entity enrollment
     * @throws OpenDidException if the entity enrollment fails
     */
    public EnrollEntityResDto enrollEntity() {
        try {
            log.debug("*** Finished enrollEntity ***");

            log.debug("\t--> 1. propose Enroll Entity");
            ProposeEnrollEntityApiResDto proposeResponse = proposeEnrollEntity();
            String txId = proposeResponse.getTxId();
            String authNonce = proposeResponse.getAuthNonce();

            log.debug("\t--> 2. request ECDH");
            EccCurveType eccCurveType = EccCurveType.SECP_256_R1;
            log.debug("\t\t--> generate Tmp Keypair");
            EcKeyPair ecKeyPair = (EcKeyPair) BaseCryptoUtil.generateKeyPair(eccCurveType);
            log.debug("\t\t--> generate ReqEcdh");
            String clientNonce = BaseMultibaseUtil.encode(BaseCryptoUtil.generateNonce(16));
            EcdhReqData reqData = generateReqData(ecKeyPair, eccCurveType, clientNonce);
            log.debug("\t\t--> request ECDH");
            RequestEcdhApiResDto ecdhResponse = requestEcdh(txId, reqData);

            log.debug("\t--> 3. request Enroll Entity");
            log.debug("\t\t--> generate DID Auth");
            DidAuth didAuth = generateDidAuth(authNonce);
            log.debug("\t\t--> request Enroll Entity");
            RequestEnrollEntityApiResDto enrollEntityResponse = requestEnrollEntity(txId, didAuth);
            log.debug("\t\t--> decrypt VC");
            VerifiableCredential vc = decryptVc((ECPrivateKey) ecKeyPair.getPrivateKey(),
                    ecdhResponse.getAccEcdh(), enrollEntityResponse, clientNonce);

            log.debug("\t--> 4. confirm Enroll Entity");
            ConfirmEnrollEntityApiResDto confirmResponse = confirmEnrollEntity(txId, vc.getId());

            log.debug("\t\t--> save VC to DB");
            certificateVcQueryService.save(CertificateVc.builder()
                    .vc(vc.toJson())
                    .build());

            log.debug("*** Finished enrollEntity ***");

            return EnrollEntityResDto.builder()
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during enrollEntity: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during enrollEntity: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_ENROLL_ENTITY_FAILED);
        }
    }

    /**
     * Propose enroll entity.
     * This method sends a propose-enroll-entity request to the TAS and returns the response as a ProposeEnrollEntityApiResDto object.
     *
     * @return the response to the propose-enroll-entity request
     */
    private ProposeEnrollEntityApiResDto proposeEnrollEntity() {
        ProposeEnrollEntityApiReqDto request = ProposeEnrollEntityApiReqDto.builder()
                .id(RandomUtil.generateMessageId())
                .build();
        return enrollFeign.proposeEnrollEntityApi(request);
    }

    /**
     * Request ECDH.
     * This method sends a request-ecdh request to the TAS and returns the response as a RequestEcdhApiResDto object.
     *
     * @param txId the transaction ID
     * @param reqEcdh the ECDH request data
     * @return the response to the request-ecdh request
     */
    private RequestEcdhApiResDto requestEcdh(String txId, EcdhReqData reqEcdh) {
        RequestEcdhApiReqDto request = RequestEcdhApiReqDto.builder()
                .id(RandomUtil.generateMessageId())
                .txId(txId)
                .reqEcdh(reqEcdh)
                .build();
        return enrollFeign.requestEcdh(request);
    }
    /**
     * Generate request data.
     * This method generates the request data for the ECDH request.
     *
     * @param publicKey the public key
     * @param curveType the ECC curve type
     * @param clientNonce the client nonce
     * @return the generated request data
     * @throws OpenDidException if the public key compression fails
     */
    private EcdhReqData generateReqData(EcKeyPair publicKey, EccCurveType curveType, String clientNonce) {
        try {
            Candidate candidate = Candidate.builder()
                    .ciphers(Arrays.asList(SymmetricCipherType.values()))
                    .build();

            String verificationMethod = "did:omn:issuer?versionId=1#keyagree";
            Proof proof = BaseCryptoUtil.generateProof(ProofType.SECP256R1_SIGNATURE_2018,
                    ProofPurpose.KEY_AGREEMENT, verificationMethod);

            EcdhReqData reqData = EcdhReqData.builder()
                    .client("did:omn:issuer")
                    .clientNonce(clientNonce)
                    .curve(curveType)
                    .publicKey(publicKey.getBase58CompreessPubKey())
                    .candidate(candidate)
                    .proof(proof)
                    .build();

            proof.setProofValue(signData(reqData, "keyagree"));

            return reqData;
        } catch (CryptoException e) {
            throw new OpenDidException(ErrorCode.CRYPTO_PUBLIC_KEY_COMPRESS_FAILED);
        }
    }

    /**
     * Request enroll entity.
     * This method sends a request-enroll-entity request to the TAS and returns the response as a RequestEnrollEntityApiResDto object.
     *
     * @param txId the transaction ID
     * @param didAuth the DID Auth object
     * @return the response to the request-enroll-entity request
     */
    private RequestEnrollEntityApiResDto requestEnrollEntity(String txId, DidAuth didAuth) {
        RequestEnrollEntityApiReqDto request = RequestEnrollEntityApiReqDto.builder()
                .id(RandomUtil.generateMessageId())
                .txId(txId)
                .didAuth(didAuth)
                .build();

        return enrollFeign.requestEnrollEntityApi(request);
    }

    /**
     * Generate DID Auth.
     * This method generates the DID Auth object for the request-enroll-entity request.
     *
     * @param authNonce the authentication nonce
     * @return the generated DID Auth object
     */
    private DidAuth generateDidAuth(String authNonce) {

        String verificationMethod = "did:omn:issuer?versionId=1#auth";

        Proof proof = BaseCryptoUtil.generateProof(ProofType.SECP256R1_SIGNATURE_2018,
                ProofPurpose.AUTHENTICATION, verificationMethod);

        DidAuth didAuth = DidAuth.builder()
                .authNonce(authNonce)
                .did("did:omn:issuer")
                .proof(proof)
                .build();

        proof.setProofValue(signData(didAuth, "auth"));
        return didAuth;
    }

    /**
     * Decrypt VC.
     * This method decrypts the Verifiable Credential received from the TAS.
     *
     * @param privateKey the private key
     * @param accEcdh the account ECDH data
     * @param enrollEntityResponse the response to the request-enroll-entity request
     * @param clientNonce the client nonce
     * @return the decrypted Verifiable Credential
     */
    private VerifiableCredential decryptVc(ECPrivateKey privateKey, AccEcdh accEcdh, RequestEnrollEntityApiResDto enrollEntityResponse, String clientNonce) {
        byte[] compressedPublicKey = BaseMultibaseUtil.decode(accEcdh.getPublicKey());
        byte[] sharedSecret = BaseCryptoUtil.generateSharedSecret(compressedPublicKey, privateKey.getEncoded(), EccCurveType.SECP_256_R1);
        byte[] mergeNonce = BaseCryptoUtil.mergeNonce(clientNonce, accEcdh.getServerNonce());
        byte[] mergeSharedSecretAndNonce = BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecret, mergeNonce, accEcdh.getCipher());

        byte[] iv = BaseMultibaseUtil.decode(enrollEntityResponse.getIv());

        byte[] decrypt = BaseCryptoUtil.decrypt(
                enrollEntityResponse.getEncVc(),
                mergeSharedSecretAndNonce,
                iv,
                accEcdh.getCipher(),
                accEcdh.getPadding()
        );

        String jsonVc = new String(decrypt);
        VerifiableCredential vc = new VerifiableCredential();
        vc.fromJson(jsonVc);

        return vc;
    }

    /**
     * Confirm enroll entity.
     * This method sends a confirm-enroll-entity request to the TAS and returns the response as a ConfirmEnrollEntityApiResDto object.
     *
     * @param txId the transaction ID
     * @param vcId the Verifiable Credential ID
     * @return the response to the confirm-enroll-entity request
     */
    private ConfirmEnrollEntityApiResDto confirmEnrollEntity(String txId, String vcId) {
        ConfirmEnrollEntityApiReqDto request = ConfirmEnrollEntityApiReqDto.builder()
                .id(RandomUtil.generateMessageId())
                .txId(txId)
                .vcId(vcId)
                .build();

        return enrollFeign.confirmEnrollEntityApi(request);
    }

    /**
     * Sign data.
     * This method signs the data using the specified key ID.
     *
     * @param source the data to sign
     * @param keyId the key ID
     * @return the signature
     */
    private String signData(Object source, String keyId) {
        String serializeSource = JsonUtil.serializeAndSort(source);
        byte[] signature = walletService.generateCompactSignature(keyId, serializeSource);

        return BaseMultibaseUtil.encode(signature);
    }
}
