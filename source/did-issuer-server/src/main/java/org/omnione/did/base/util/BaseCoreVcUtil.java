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

package org.omnione.did.base.util;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.VcManager;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.provider.ProviderDetail;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.DocumentVerificationEvidence;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class BaseCoreVcUtil {

    /**
     * Parse VC Schema
     *
     * @param vcSchemaJson VC Schema JSON
     * @return VC Schema object
     */
    public static VcSchema parseVcSchema(String vcSchemaJson) {
        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(vcSchemaJson);

        return vcSchema;
    }

    /**
     * Set Issuer
     * @param issueVcParam Issue VC Param
     * @param issuerDid Issuer DID
     * @param issuerName Issuer Name
     * @param certVcRef The reference of the certificate VC.
     *
     */
    public static void setIssuer(IssueVcParam issueVcParam, String issuerDid, String issuerName, String certVcRef) {
        ProviderDetail providerDetail = new ProviderDetail();
        providerDetail.setDid(issuerDid);
        providerDetail.setName(issuerName);
        providerDetail.setCertVcRef(certVcRef);
        issueVcParam.setProviderDetail(providerDetail);
    }

    /**
     * Set VC Schema
     * @param issueVcParam Issue VC Param
     * @param vcSchemaJson VC Schema JSON
     */
    public static void setVcSchema(IssueVcParam issueVcParam, String vcSchemaJson) {
        VcSchema vcSchema = parseVcSchema(vcSchemaJson);
        issueVcParam.setVcSchema(vcSchema);
    }

    /**
     * Set Claim Info
     * @param issueVcParam Issue VC Param
     * @param claimInfoMap Claim Info Map
     */
    public static void setClaimInfo(IssueVcParam issueVcParam, HashMap<String, ClaimInfo> claimInfoMap) {
        issueVcParam.setPrivacy(claimInfoMap);
    }

    /**
     * Set VC Type
     * @param issueVcParam Issue VC Param
     * @param vcTypeList VC Type List
     */
    public static void setVcTypes(IssueVcParam issueVcParam, List<VcType> vcTypeList) {
        issueVcParam.setVcType(vcTypeList);
    }

    /**
     * Set Evidence
     * @param issueVcParam Issue VC Param
     * @param evidence Evidence
     */
    public static void setEvidence(IssueVcParam issueVcParam, DocumentVerificationEvidence evidence) {
        issueVcParam.setEvidences(List.of(evidence));
    }

    /**
     * Generates a Verifiable Credential (VC).
     *
     * @param issueVcParam The VC issuance parameters.
     * @param did The DID of the recipient (VC issuance target).
     * @return The generated Verifiable Credential.
     * @throws OpenDidException if the VC generation fails.
     */
    public static VerifiableCredential generateVc(IssueVcParam issueVcParam, String did) {
        try {
            VcManager vcManager = new VcManager();
            return vcManager.issueCredential(issueVcParam, did);
        } catch (CoreException e) {
            log.error("Failed to generate VC: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_GENERATION_FAILED);
        }
    }

    /**
     * Set Validate Until
     * @param issueVcParam Issue VC Param
     * @param years Years
     */
    public static void setValidateUntil(IssueVcParam issueVcParam, int years) {
        issueVcParam.setValidFrom(DateTimeUtil.getCurrentUTCTime());
        issueVcParam.setValidUntil(DateTimeUtil.addYearsToCurrentTime(years));
    }
    /**
     * Generate VC Meta
     * @param vc Verifiable Credential
     * @return VC Meta
     */
    public static VcMeta generateVcMeta(VerifiableCredential vc, String certVcRef) {
        VcManager vcManager = new VcManager();

        return vcManager.generateVcMetaData(vc, certVcRef);
    }
}
