package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.spi.ProofKeyResolver.ResolvedProofKey;

import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DidProofKeyResolverTest {

    private static final String HOLDER_DID = "did:omn:holder";
    private static final String KEY_ID = HOLDER_DID + "?versionId=1#bio";
    private static final String PIN_KEY_ID = HOLDER_DID + "?versionId=1#pin";

    @Mock
    private StorageService storageService;

    private DidProofKeyResolver resolver;
    private ECKey holderKey;
    private ECPublicKey holderPublicKey;

    @BeforeEach
    void setUp() throws Exception {
        resolver = new DidProofKeyResolver(storageService);
        holderKey = new ECKeyGenerator(Curve.P_256).generate();
        holderPublicKey = holderKey.toECPublicKey();
    }

    @Test
    void resolvesShortAuthenticationReferenceToPublicJwk() throws Exception {
        when(storageService.findDidDoc(HOLDER_DID)).thenReturn(
                didDocument(HOLDER_DID, List.of("bio"), HOLDER_DID));

        ResolvedProofKey resolved = resolver.resolve(KEY_ID);

        assertThat(resolved.subjectDid()).isEqualTo(HOLDER_DID);
        assertThat(resolved.publicJwk())
                .containsEntry("kty", "EC")
                .containsEntry("crv", "P-256")
                .containsEntry("kid", KEY_ID);
    }

    @Test
    void resolvedDidKeyVerifiesTheIssuanceProofSignature() throws Exception {
        when(storageService.findDidDoc(HOLDER_DID)).thenReturn(
                didDocument(HOLDER_DID, List.of("bio"), HOLDER_DID));
        SignedJWT proof = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.ES256)
                        .type(new JOSEObjectType("openid4vci-proof+jwt"))
                        .keyID(KEY_ID)
                        .build(),
                new JWTClaimsSet.Builder()
                        .audience("https://issuer.example.com")
                        .issueTime(new Date())
                        .claim("nonce", "nonce-1")
                        .build());
        proof.sign(new ECDSASigner(holderKey));

        ResolvedProofKey resolved = resolver.resolve(KEY_ID);
        ECKey resolvedJwk = ECKey.parse(resolved.publicJwk());

        assertThat(proof.verify(new ECDSAVerifier(resolvedJwk))).isTrue();
    }

    @Test
    void rejectsKeyThatIsNotAnAuthenticationMethod() {
        when(storageService.findDidDoc(HOLDER_DID)).thenReturn(
                didDocument(HOLDER_DID, List.of("pin"), HOLDER_DID));

        assertThatThrownBy(() -> resolver.resolve(KEY_ID))
                .isInstanceOf(OID4VCIException.class)
                .hasMessageContaining("authentication");
    }

    @Test
    void resolvesPinVerificationMethodControlledByTas() throws Exception {
        when(storageService.findDidDoc(HOLDER_DID)).thenReturn(
                didDocument(HOLDER_DID, List.of("pin"), "did:omn:tas", "pin"));

        ResolvedProofKey resolved = resolver.resolve(PIN_KEY_ID);

        assertThat(resolved.subjectDid()).isEqualTo(HOLDER_DID);
        assertThat(resolved.publicJwk()).containsEntry("kid", PIN_KEY_ID);
    }

    private DidDocument didDocument(
            String documentId, List<String> authentication, String methodController) {
        return didDocument(documentId, authentication, methodController, "bio");
    }

    private DidDocument didDocument(
            String documentId, List<String> authentication, String methodController, String methodId) {
        VerificationMethod method = new VerificationMethod();
        method.setId(methodId);
        method.setController(methodController);
        method.setPublicKeyMultibase(BaseMultibaseUtil.encode(
                compressedPublicKey(holderPublicKey), MultiBaseType.base64));

        DidDocument document = new DidDocument();
        document.setId(documentId);
        document.setAuthentication(authentication);
        document.setVerificationMethod(List.of(method));
        return document;
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
}
