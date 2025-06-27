/*
 * Copyright 2025 OmniOne.
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
package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ZkpAttribute;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.VerifyNamespaceIdUniqueResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpAttributeDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpAttributeSaveDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceInfoDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceSaveDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceUpdateDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpNamespaceQueryService;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpNamespaceService {
    private final ZkpNamespaceQueryService zkpNamespaceQueryService;

    public PageImpl<ZkpNamespaceDto> searchZkpNamespaceList(String searchKey, String searchValue, Pageable pageable) {
       return zkpNamespaceQueryService.searchZkpNamespaceList(searchKey, searchValue, pageable);
    }

    public EmptyResDto createZkpNamespace(ZkpNamespaceInfoDto request) {

        try {
            // Save ZKP Namespace
            log.debug("Saving ZKP Namespace: {}", request.getNamespace());
            ZkpNamespace zkpNamespace = ZkpNamespace.builder()
                    .namespaceId(request.getNamespace()
                            .getNamespaceId())
                    .name(request.getNamespace()
                            .getName())
                    .ref(request.getNamespace()
                            .getRef())
                    .build();

            // Save ZKP Attributes
            log.debug("Saving ZKP Attributes: {}", request.getAttributes());
            ZkpNamespace saveZkpNamespace = zkpNamespaceQueryService.save(zkpNamespace);

            List<ZkpAttribute> attributes = request.getAttributes()
                    .stream()
                    .map(attr -> ZkpAttribute.builder()
                            .label(attr.getLabel())
                            .type(AttributeDef.ATTR_TYPE.valueOf(attr.getType()))
                            .caption(attr.getCaption())
                            .zkpNamespaceId(saveZkpNamespace.getId())
                            .build())
                    .toList();

            zkpNamespaceQueryService.saveAllAttributes(attributes);
        } catch(OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while saving ZKP Namespace and Attributes", e);
            throw new OpenDidException(ErrorCode.ZKP_NAMESPACE_SAVE_FAILED);
        }

        return new EmptyResDto();
    }

    public ZkpNamespaceInfoDto getZkpNamespaceInfoById(Long id) {
        try {
            // Fetch ZKP Namespace
            log.debug("Fetching ZKP Namespace by ID: {}", id);
            ZkpNamespace zkpNamespace = zkpNamespaceQueryService.findNamespaceById(id);

            // Fetch ZKP Attributes
            log.debug("Fetching ZKP Attributes for Namespace ID: {}", zkpNamespace.getId());
            List<ZkpAttribute> zkpAttributeList = zkpNamespaceQueryService.findAttributesByNamespaceId(zkpNamespace.getId());

            ZkpNamespaceSaveDto zkpNamespaceSaveDto = ZkpNamespaceSaveDto.builder()
                    .namespaceId(zkpNamespace.getNamespaceId())
                    .name(zkpNamespace.getName())
                    .ref(zkpNamespace.getRef())
                    .build();

            List<ZkpAttributeSaveDto> zkpAttributeSaveDtoList = zkpAttributeList.stream()
                    .map(zkpAttribute -> ZkpAttributeSaveDto.builder()
                            .label(zkpAttribute.getLabel())
                            .type(zkpAttribute.getType()
                                    .name())
                            .caption(zkpAttribute.getCaption())
                            .build())
                    .toList();

            return ZkpNamespaceInfoDto.builder()
                    .namespace(zkpNamespaceSaveDto)
                    .attributes(zkpAttributeSaveDtoList)
                    .build();
        } catch(OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while fetching ZKP Namespace and Attributes", e);
            throw new OpenDidException(ErrorCode.ZKP_NAMESPACE_RETRIEVAL_FAILED);
        }
    }

    public EmptyResDto updateZkpNamespace(ZkpNamespaceUpdateDto request) {
        try {
            // Fetch ZKP Namespace
            log.debug("Fetching ZKP Namespace by ID: {}", request.getId());
            ZkpNamespace zkpNamespace = zkpNamespaceQueryService.findNamespaceById(request.getId());

            // Update ZKP Namespace
            zkpNamespace.setName(request.getNamespace()
                    .getName());
            zkpNamespace.setRef(request.getNamespace()
                    .getRef());

            List<ZkpAttribute> attributeList = zkpNamespaceQueryService.findAttributesByNamespaceId(request.getId());

            // Delete old attributes
            log.debug("Deleting old ZKP Attributes for Namespace ID: {}", request.getId());
            zkpNamespaceQueryService.deleteAttributesByZkpNamespaceId(request.getId());

            // Save ZKP Attributes
            log.debug("Saving new ZKP Attributes: {}", request.getAttributes());
            List<ZkpAttribute> attributes = request.getAttributes()
                    .stream()
                    .map(attr -> ZkpAttribute.builder()
                            .label(attr.getLabel())
                            .type(AttributeDef.ATTR_TYPE.valueOf(attr.getType()))
                            .caption(attr.getCaption())
                            .zkpNamespaceId(zkpNamespace.getId())
                            .build())
                    .toList();

            zkpNamespaceQueryService.saveAllAttributes(attributes);

            return new EmptyResDto();
        } catch(OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while updating ZKP Namespace and Attributes", e);
            throw new OpenDidException(ErrorCode.ZKP_NAMESPACE_UPDATE_FAILED);
        }
    }

    public void deleteZkpNamespaceById(Long id) {
        try {
            zkpNamespaceQueryService.deleteZkpNamespaceById(id);
        } catch(OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while deleting ZKP Namespace", e);
            throw new OpenDidException(ErrorCode.ZKP_NAMESPACE_DELETE_FAILED);
        }
    }

    public VerifyNamespaceIdUniqueResDto verifyNamespaceIdUnique(String namespaceId) {
        long count = zkpNamespaceQueryService.countByNamespaceId(namespaceId);
        return VerifyNamespaceIdUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    public List<ZkpNamespaceDto> getAllNamespaces() {
        List<ZkpNamespace> zkpNamespaceList = zkpNamespaceQueryService.findAllNamespaces();
        return zkpNamespaceList.stream()
                .map(ZkpNamespaceDto::fromEntity)
                .toList();
    }

    public List<ZkpAttributeDto> getAttributesByNamespaceId(Long zkpNamespaceId) {
        List<ZkpAttribute> zkpAttributeList = zkpNamespaceQueryService.findAttributesByNamespaceId(zkpNamespaceId);
        return zkpAttributeList.stream()
                .map(ZkpAttributeDto::fromEntity)
                .toList();
    }
}
