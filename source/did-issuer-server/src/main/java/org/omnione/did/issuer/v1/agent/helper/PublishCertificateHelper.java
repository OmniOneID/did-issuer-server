package org.omnione.did.issuer.v1.agent.helper;

import org.omnione.did.sdk.issuer.model.domain.SdkIssuerInfo;

import org.omnione.did.sdk.issuer.provider.CertificateVcProvider;

public class PublishCertificateHelper implements CertificateVcProvider {

    /**
     * Retrieves the Verifiable Credential (VC) URL for the Issuer certificate.
     * If the published certificate URL is available, it returns that; otherwise, it
     * returns the certificate URL.
     *
     * @param issuerInfo The IssuerInfo object containing the URLs.
     * @return The appropriate VC URL for the Issuer certificate.
     */
    public static String buildCertificateVcUrl(SdkIssuerInfo issuerInfo) {
        return issuerInfo.getPublishedCertificateUrl() != null ? issuerInfo.getPublishedCertificateUrl()
                : issuerInfo.getCertificateUrl();
    }

    public static String buildCertificateVcUrl(org.omnione.did.base.db.domain.IssuerInfo issuerInfo) {
        return issuerInfo.getPublishedCertificateUrl() != null ? issuerInfo.getPublishedCertificateUrl()
                : issuerInfo.getCertificateUrl();
    }

    public static String getCertificateVcURL(org.omnione.did.base.db.domain.IssuerInfo issuerInfo) {
        return buildCertificateVcUrl(issuerInfo);
    }

    @Override
    public String getCertificateVcURL(SdkIssuerInfo issuerInfo) {
        return PublishCertificateHelper.buildCertificateVcUrl(issuerInfo);
    }
}
