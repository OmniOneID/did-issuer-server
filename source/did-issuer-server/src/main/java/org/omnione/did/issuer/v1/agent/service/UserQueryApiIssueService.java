package org.omnione.did.issuer.v1.agent.service;

import org.omnione.did.issuer.v1.agent.service.sample.SampleData;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;
import org.omnione.did.sdk.issuer.model.dto.UserInfoDto;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.issuer.v1.agent.service.query.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * This service provides methods for issuing a Verifiable Credential (VC) for a
 * national ID.
 */
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;

@Slf4j
@Service
@Transactional
@Profile("!sample")
public class UserQueryApiIssueService extends AbstractPersistentIssueVcService {
    private final UserQueryService userQueryService;

    public UserQueryApiIssueService(IssueVcProtocol issueVcProtocol,
                                    SpringIssueDataProvider issuerDataProvider,
                                    TransactionService transactionService,
                                    UserQueryService userQueryService,
                                    StorageService storageService) {
        super(issueVcProtocol, issuerDataProvider, transactionService, storageService);
        this.userQueryService = userQueryService;
    }

    @Override
    protected UserInfoDto findUserByVcProfileAndVcSchemaId(SdkVcProfile vcProfile, SdkIssueProfile issueProfile) {

        User user = userQueryService.findByIdAndVcSchemaId(vcProfile.getUserId(), issueProfile.getVcSchemaId());

        UserInfoDto resultUser = UserInfoDto.builder()
                .id(user.getId())
                .vcSchemaId(user.getVcSchemaId())
                .pii(user.getPii())
                .did(user.getDid())
                .build();

        String sampleData = getSampleData(issueProfile.getVcPlanId(), user.getPii());

        // BasicInfoRequest request = BasicInfoRequest.builder()
        // .apiKey(safeExtractFirstHeaderValue(issueProfile))
        // .url(issueProfile.getUserQueryUrl())
        // .studentId(user.getPii())
        // .build();
        //
        // try (MiddlewareClient client = new MiddlewareClient()) {
        // DetailInfoResponse response = client.getDetailInfo(request);
        //
        // if (!response.isResult()) {
        // String msg = (response.getMessage() != null)
        // ? response.getMessage()
        // : ErrorCode.USER_QUERY_FAILED.getMessage();
        // throw new OpenDidException(ErrorCode.USER_QUERY_FAILED, msg);
        // }
        //
        // Map<String, Object> data = response.getData();
        // resultUser.setData(JsonUtil.serializeToJson(data));
        //
        // resultUser.setValidFrom(parseZonedDateTime(data.get("cr.ac.student_id.emision_date")));
        // resultUser.setValidUntil(parseZonedDateTimeUntil(data.get("cr.ac.student_id.expiration_date")));
        // } catch (OpenDidException e) {
        // throw e;
        // } catch (Exception e) {
        // throw new OpenDidException(ErrorCode.EXTERNAL_SERVER_INTEGRATION_ERROR,
        // e.getMessage());
        // }

        resultUser.setData(sampleData);
        return resultUser;
    }

    private ZonedDateTime parseZonedDateTime(Object value) {
        if (value == null) {
            return ZonedDateTime.now();
        }
        try {
            return ZonedDateTime.parse(value.toString());
        } catch (DateTimeParseException e) {
            throw new OpenDidException(ErrorCode.INVALID_DATE_FORMAT, "Invalid date format: " + value);
        }
    }

    private ZonedDateTime parseZonedDateTimeUntil(Object value) {
        if (value == null) {
            return DateTimeUtil.addYearsToCurrentTime(2);
        }
        try {
            return ZonedDateTime.parse(value.toString());
        } catch (DateTimeParseException e) {
            throw new OpenDidException(ErrorCode.INVALID_DATE_FORMAT, "Invalid date format: " + value);
        }
    }

    private String safeExtractFirstHeaderValue(SdkIssueProfile issueProfile) {
        if (issueProfile.getUserQueryHeaders() == null || issueProfile.getUserQueryHeaders().isEmpty()) {
            return null;
        }
        final var first = issueProfile.getUserQueryHeaders().getFirst();
        return (first != null) ? first.getValue() : null;
    }

    @Override
    protected SdkUser findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        // User Query Type API
        // Store user info for managing VC status
        User user = userQueryService.findByPiiAndVcSchemaId(holder.getPii(), vcSchemaId).orElseGet(
                () -> userQueryService.save(User.builder()
                        .vcSchemaId(vcSchemaId)
                        .pii(holder.getPii())
                        .did(holder.getDid())
                        .vcSchemaId(vcSchemaId)
                        .build()));
        user.setDid(holder.getDid());
        if (user.getDid() == null) {
            user.setDid(holder.getDid());
        }
        return toSdkUser(user);
    }

    private SdkUser toSdkUser(User user) {
        if (user == null) {
            return null;
        }
        return SdkUser.builder()
                .id(user.getId())
                .pii(user.getPii())
                .did(user.getDid())
                .vcSchemaId(user.getVcSchemaId())
                .data(user.getData())
                .build();
    }


    private String getSampleData(String vcPlanId, String pii) {
        SampleData sampleData = new SampleData();
        if (!sampleData.getSampleDataByPii(pii).containsKey(vcPlanId)) {
            throw new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND);
        }
        return sampleData.getSampleDataByPii(pii).get(vcPlanId);
    }
}
