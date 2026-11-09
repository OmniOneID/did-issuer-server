package org.omnione.did.issuer.v1.admin.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoFromDemoReqDto;
import org.omnione.did.issuer.v1.admin.service.query.UserInfoQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {
    @Mock UserInfoQueryService userQueryService;
    @Mock VcSchemaQueryService vcSchemaQueryService;

    @Test
    void userInitWithNewDidDoesNotReuseUserWithSamePii() {
        var service = new UserManagementService(userQueryService, vcSchemaQueryService);
        when(vcSchemaQueryService.findByVcSchemaId("vc.schema.driver.license"))
                .thenReturn(VcSchema.builder().id(2L).build());
        when(userQueryService.findByDidAndVcSchemaIdOrNew("did:omn:second", 2L))
                .thenReturn(new User());

        service.createUserInfo(CreateUserInfoFromDemoReqDto.builder()
                .did("did:omn:second")
                .pii("same-pii")
                .vcSchemaId("vc.schema.driver.license")
                .userInfo("{\"licenseNumber\":\"2\"}")
                .build());

        var saved = ArgumentCaptor.forClass(User.class);
        verify(userQueryService).save(saved.capture());
        assertEquals("did:omn:second", saved.getValue().getDid());
        assertEquals(2L, saved.getValue().getVcSchemaId());
        verify(userQueryService, never()).findByPiiAndVcSchemaIdOrNew(any(), any());
    }
}
