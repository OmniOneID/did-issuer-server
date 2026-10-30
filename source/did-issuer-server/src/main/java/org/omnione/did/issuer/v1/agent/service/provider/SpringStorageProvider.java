package org.omnione.did.issuer.v1.agent.service.provider;

import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.provider.StorageProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringStorageProvider implements StorageProvider {

    private final StorageService storageService;

    @Override
    public DidDocument findDidDoc(String did) {
        return storageService.findDidDoc(did);
    }

    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        storageService.registerVcMeta(vcMeta);
    }

    @Override
    public VcMeta getVcMetaByVcId(String vcId) {
        return storageService.getVcMetaByVcId(vcId);
    }

    @Override
    public void updateVcStatus(String vcId, VcStatus status) {
        storageService.updateVcStatus(vcId, status);
    }
}
