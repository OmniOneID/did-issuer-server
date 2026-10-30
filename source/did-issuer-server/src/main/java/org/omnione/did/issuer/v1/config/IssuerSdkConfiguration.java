package org.omnione.did.issuer.v1.config;

import lombok.RequiredArgsConstructor;
import org.omnione.did.issuer.v1.agent.helper.PublishCertificateHelper;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.provider.SpringStorageProvider;
import org.omnione.did.issuer.v1.agent.service.provider.SpringVcStatusDataProvider;
import org.omnione.did.issuer.v1.agent.service.provider.SpringWalletProvider;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocolImpl;
import org.omnione.did.sdk.issuer.protocol.vc.VcStatusProtocol;
import org.omnione.did.sdk.issuer.protocol.vc.VcStatusProtocolImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!sample")
public class IssuerSdkConfiguration {

    private final SpringIssueDataProvider issueDataProvider;
    private final SpringWalletProvider walletProvider;
    private final SpringStorageProvider storageProvider;
    private final SpringVcStatusDataProvider vcStatusDataProvider;

    @Bean
    public IssueVcProtocol issueVc() {
        IssueVcProtocolImpl manager = new IssueVcProtocolImpl(issueDataProvider, walletProvider, storageProvider);
        manager.setCertificatePublisher(new PublishCertificateHelper());
        return manager;
    }

    @Bean
    public VcStatusProtocol vcStatus() {
        return new VcStatusProtocolImpl(vcStatusDataProvider, storageProvider);
    }
}
