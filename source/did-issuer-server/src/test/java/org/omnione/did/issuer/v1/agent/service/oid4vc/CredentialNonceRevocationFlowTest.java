package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.issuer.v1.agent.controller.oid4vc.CredentialIssuanceController;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.CredentialManagementService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.oid4vc.oid4vci.dto.nonce.NonceResponse;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationResponse;
import org.omnione.did.oid4vc.oid4vci.revocation.service.CredentialRevocationTokenVerifier;
import org.omnione.did.oid4vc.oid4vci.service.CredentialService;
import org.omnione.did.oid4vc.oid4vci.service.store.CNonceStore;
import org.omnione.did.oid4vc.oid4vci.service.store.memory.InMemoryCNonceStore;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;

import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialNonceRevocationFlowTest {

    private static final Instant NOW = Instant.parse("2026-10-14T01:00:00Z");
    private static final Instant REVOKED_AT = Instant.parse("2026-10-14T01:02:03Z");
    private static final String HOLDER_DID = "did:omn:holder";
    private static final String KEY_ID = HOLDER_DID + "?versionId=1#bio";
    private static final String STATUS_LIST_URI = "https://issuer.example/status-lists/1";
    private static final long STATUS_LIST_INDEX = 42L;

    @Mock
    private Oid4vcCredentialIssuanceRepository issuanceRepository;
    @Mock
    private CredentialManagementService credentialManagementService;
    @Mock
    private StorageService storageService;

    private ECKey holderKey;
    private Oid4vcCredentialIssuanceEntity issuance;

    @BeforeEach
    void setUp() throws Exception {
        holderKey = new ECKeyGenerator(Curve.P_256).keyID(KEY_ID).generate();

        issuance = new Oid4vcCredentialIssuanceEntity();
        issuance.setIssuanceId("issuance-1");
        issuance.setUserId("user-1");
        issuance.setSubjectDid(HOLDER_DID);
        issuance.setIssuanceState(IssuanceState.ISSUED);
        issuance.setCredentialStatus(CredentialStatus.VALID);

        when(issuanceRepository.findByStatusReferenceForUpdate(
                STATUS_LIST_URI, STATUS_LIST_INDEX)).thenReturn(Optional.of(issuance));
        when(storageService.findDidDoc(HOLDER_DID)).thenReturn(didDocument(holderKey.toECPublicKey()));
        when(credentialManagementService.changeStatus(
                "issuance-1", CredentialStatus.INVALID, "USER_REQUEST", HOLDER_DID))
                .thenReturn(issuanceView());
    }

    @Test
    void issuesNonceThenRevokesCredentialWithSignedHolderProof() throws Exception {
        CNonceStore nonceStore = new InMemoryCNonceStore();
        CredentialService credentialService = credentialService(nonceStore);
        CredentialRevocationService revocationService = new CredentialRevocationService(
                new CredentialRevocationTokenVerifier(Clock.fixed(NOW, ZoneOffset.UTC)),
                issuanceRepository, credentialManagementService, nonceStore, storageService);
        CredentialIssuanceController controller = new CredentialIssuanceController(
                credentialService, revocationService);

        NonceResponse nonceResponse = controller.handleNonce();
        String cNonce = nonceResponse.getCNonce();

        assertThat(cNonce).isNotBlank();
        assertThat(nonceStore.contains(cNonce)).isTrue();

        CredentialRevocationRequest request = new CredentialRevocationRequest();
        request.setRevocationToken(createRevocationToken(cNonce));

        CredentialRevocationResponse response = controller.revokeCredential(request);

        assertThat(response.credentialStatus().status()).isEqualTo("INVALID");
        assertThat(response.credentialStatus().statusList().uri()).isEqualTo(STATUS_LIST_URI);
        assertThat(response.credentialStatus().statusList().idx()).isEqualTo(STATUS_LIST_INDEX);
        assertThat(response.revokedAt()).isEqualTo(REVOKED_AT);
        assertThat(nonceStore.contains(cNonce)).isFalse();

        assertThatThrownBy(() -> controller.revokeCredential(request))
                .isInstanceOfSatisfying(OpenDidException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ISSUER_NONCE_INVALID));
        verify(credentialManagementService, times(1)).changeStatus(
                "issuance-1", CredentialStatus.INVALID, "USER_REQUEST", HOLDER_DID);
        verify(storageService, times(2)).findDidDoc(HOLDER_DID);
    }

    private CredentialService credentialService(CNonceStore nonceStore) {
        return new CredentialService(
                null, null, null, null, null, null, null,
                null, null, null, null, nonceStore, Optional.empty());
    }

    private String createRevocationToken(String cNonce) throws Exception {
        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.ES256)
                        .type(new JOSEObjectType(CredentialRevocationTokenVerifier.TOKEN_TYPE))
                        .keyID(KEY_ID)
                        .build(),
                new JWTClaimsSet.Builder()
                        .issuer(HOLDER_DID)
                        .issueTime(Date.from(NOW))
                        .expirationTime(Date.from(NOW.plusSeconds(300)))
                        .claim("nonce", cNonce)
                        .claim("credential_status", Map.of(
                                "status_list", Map.of(
                                        "uri", STATUS_LIST_URI,
                                        "idx", STATUS_LIST_INDEX)))
                        .build());
        jwt.sign(new ECDSASigner(holderKey));
        return jwt.serialize();
    }

    private DidDocument didDocument(ECPublicKey publicKey) {
        VerificationMethod verificationMethod = new VerificationMethod();
        verificationMethod.setId("bio");
        verificationMethod.setController(HOLDER_DID);
        verificationMethod.setPublicKeyMultibase(BaseMultibaseUtil.encode(
                compressedPublicKey(publicKey), MultiBaseType.base64));

        DidDocument didDocument = new DidDocument();
        didDocument.setId(HOLDER_DID);
        didDocument.setAuthentication(List.of("bio"));
        didDocument.setVerificationMethod(List.of(verificationMethod));
        return didDocument;
    }

    private byte[] compressedPublicKey(ECPublicKey publicKey) {
        byte[] x = unsignedCoordinate(publicKey.getW().getAffineX());
        byte[] compressed = new byte[33];
        compressed[0] = publicKey.getW().getAffineY().testBit(0) ? (byte) 0x03 : (byte) 0x02;
        System.arraycopy(x, 0, compressed, 1, x.length);
        return compressed;
    }

    private byte[] unsignedCoordinate(BigInteger coordinate) {
        byte[] encoded = coordinate.toByteArray();
        if (encoded.length == 32) {
            return encoded;
        }
        if (encoded.length > 32) {
            return Arrays.copyOfRange(encoded, encoded.length - 32, encoded.length);
        }

        byte[] padded = new byte[32];
        System.arraycopy(encoded, 0, padded, 32 - encoded.length, encoded.length);
        return padded;
    }

    private CredentialIssuanceView issuanceView() {
        return new CredentialIssuanceView(
                "issuance-1", "user-1", "config-1", "dc+sd-jwt-did",
                STATUS_LIST_URI, STATUS_LIST_INDEX, CredentialStatus.INVALID,
                IssuanceState.ISSUED, NOW.minusSeconds(3600), null, REVOKED_AT,
                null, null, null, NOW.minusSeconds(3600), REVOKED_AT);
    }
}
