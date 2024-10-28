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

package org.omnione.did.base.exception;

import lombok.Getter;

/**
 * Enumeration of error codes used in the DID Verifier system.
 * Each error code contains a unique identifier, a descriptive message, and an associated HTTP status code.
 *
 */
@Getter
public enum ErrorCode {
    TODO("9999", "TODO", 999),

    // 000~ 099 = Transaction,
    TRANSACTION_NOT_FOUND("00001", "The transaction does not exist.", 400),
    TRANSACTION_INVALID("00002", "The transaction is not valid.", 400),
    TRANSACTION_EXPIRED("00003", "The transaction has expired.", 400),
    REF_ID_INVALID("00004", "The refId is not valid.", 400),

    TR_VC_OFFER_FAILED("00005", "Failed to process the 'request-offer' API request.", 500),
    TR_VC_ISSUE_PROPOSE_FAILED("00006", "Failed to process the 'inspect-propose-issue' API request.",500),
    TR_VC_ISSUE_PROFILE_FAILED("00007", "Failed to process the 'generate-issue-profile' API request.", 500),
    TR_VC_ISSUE_FAILED("00008", "Failed to process the 'issue-vc' API request.", 500),
    TR_VC_ISSUE_COMPLETE_FAILED("00009", "Failed to process the 'complete-vc' API request.", 500),

    TR_VC_REVOKE_PROPOSE_FAILED("00010","Failed to process the 'inspect-propose-revoke' API request.", 500),
    TR_VC_REVOKE_FAILED("00011", "Failed to process the 'revoke-vc' API request.", 500),
    TR_VC_REVOKE_COMPLETE_FAILED("00012", "Failed to process the 'complete-revoke-vc' API request.", 500),

    TR_VC_ISSUE_RESULT_FAILED("00013", "Failed to process the 'issue-vc-result' API request.", 500),

    TR_ENROLL_ENTITY_FAILED("00014", "Failed to process the 'issue-certificate-vc' API request.", 500),
    TR_GET_CERTIFICATE_VC_FAILED("00015", "Failed to process the 'get-certificate-vc' API request.", 500),

    TR_VC_UPDATE_STATUS_FAILED("00016", "Failed to process the 'update-vc-status' API request.", 500),

    // 100~ 199 = DID
    DID_DOC_FIND_FAILURE("000100", "Failed to find DID Document.", 500),
    DID_DOC_VERSION_INVALID("00101", "Invalid DID Document version.", 400),

    // 200~ 299 = VC
    VC_OFFER_NOT_FOUND("00200", "The Offer does not exist.", 400),
    VC_OFFER_EXPIRED("00201", "The Offer has expired.", 400),
    VC_PLAN_ID_INVALID("00202", "The vcPlanId is not valid.", 400),
    VC_PROFILE_INVALID("00203", "The profile id is not valid.", 400),
    VC_PROFILE_ISSUER_NONCE_INVALID("00204", "The profile issuer nonce is not valid.", 400),
    VC_ISSUE_FAILED("00205", "Failed to Issue VC", 500),
    VC_NOT_FOUND("00206", "The VC does not exist.", 400),
    VC_ID_NOT_MATCH("00207", "VC ID does not match.", 400),
    PARSE_REQUEST_VC_FAILURE("00208", "Failed to parse Request VC.", 400),
    REVOKED_VC("00209", "This VC is Revoked.", 400),
    ISSUER_NONCE_INVALID("00210", "The issuer nonce is not valid.", 400),
    VC_SCHEMA_NAME_INVALID("00211", "VC Schema name is not valid", 400),
    VC_GENERATION_FAILED("00212", "Failed to generate VC", 500),
    VC_SCHEMA_PARSE_FAILED("00213", "Failed to parse VC Schema", 500),


    // 300~ 399 = Holder
    HOLDER_NOT_FOUND("00300", "The Holder does not exist.", 400),
    USER_NOT_FOUND("00301", "The User dose not exist.", 400),
    HOLDER_INVALID("00302", "The Holder is not valid.", 400),

    // 400~ 499 = SDK(enc, dec, ...)
    CRYPTO_ENCODING_FAILED("00400", "Failed to encoding data.", 500),
    CRYPTO_DECODING_FAILED("00401", "Failed to decoding data.", 400),
    CRYPTO_ENCRYPTION_FAILED("00402", "Failed to encrypt data.", 500),
    CRYPTO_DECRYPTION_FAILED("00403", "Failed to decrypt data.", 500),
    CRYPTO_NONCE_GENERATION_FAILED("00404", "Failed to generate nonce.", 500),
    CRYPTO_INITIAL_VECTOR_GENERATION_FAILED("00405", "Failed to generate initial vector.", 500),
    CRYPTO_SESSION_KEY_GENERATION_FAILED("00406", "Failed to generate session key.", 500),
    CRYPTO_NONCE_AND_SHARED_SECRET_MERGE_FAILED("00407", "Failed to merge nonce and shared secret.", 500),
    CRYPTO_NONCE_MERGE_FAILED("00408", "Failed to merge nonce.", 500),
    CRYPTO_SYMMETRIC_CIPHER_TYPE_INVALID("00409", "Invalid symmetric cipher type.", 500),
    CRYPTO_KEY_PAIR_GENERATION_FAILED("00410", "Failed to generate key pair.", 500),
    CRYPTO_PUBLIC_KEY_UN_COMPRESS_FAILED("00411", "Failed to uncompress public key.", 500),
    CRYPTO_PUBLIC_KEY_COMPRESS_FAILED("00412", "Failed to compress public key.", 500),

    SIGNATURE_VERIFICATION_FAILED("00413", "Failed to Signature verification.", 400),
    SIGNATURE_GENERATION_FAILED("00415", "Failed to generate signature.", 500),
    DIGEST_HASH_GENERATION_FAILURE("00416", "Failed to generate hash value.", 500),
    WALLET_INFO_NOT_FOUND("00417", "Wallet is not registered.", 400),
    WALLET_CONNECT_FAILURE("00418", "Failed to connect wallet.", 500),
    WALLET_CREATION_FAILURE("00419", "Failed to create wallet.", 500),

    GET_SIGN_KEY_IDS_FAILED("00420", "Failed to get sign key ids", 400),
    GET_SIGN_DATA_FAILED("00421", "Failed to get sign data", 400),
    GET_VERIFICATION_METHOD_FAILED("00422", "Failed to retrieve verification method.", 500),
    HASH_GENERATION_FAILED("00423", "Failed to generate hash value.", 500),




    // 500~ 599 = Issuer Error
    CERTIFICATE_DATA_NOT_FOUND("00501", "Certificate VC data not found.", 500),

    // 600~ 699 = B/C
    BLOCKCHAIN_INITIALIZATION_FAILED("00600", "Failed to initialize blockchain.", 500),
    BLOCKCHAIN_DIDDOC_REGISTRATION_FAILED("00601", "Failed to register DID Document on the blockchain.", 500),
    BLOCKCHAIN_GET_DID_DOC_FAILED("00602", "Failed to retrieve DID document on the blockchain.", 500),
    BLOCKCHAIN_UPDATE_DID_DOC_FAILED("00603", "Failed to update DID Document on the blockchain.", 500),
    BLOCKCHAIN_VC_META_REGISTRATION_FAILED("00604", "Failed to register VC meta on the blockchain.", 500),
    BLOCKCHAIN_VC_META_RETRIEVAL_FAILED("00605", "Failed to retrieve VC meta on the blockchain.", 500),
    BLOCKCHAIN_VC_STATUS_UPDATE_FAILED("00606", "Failed to update VC status on the blockchain.", 500),


    // 700~ 799 = etc
    JSON_SERIALIZE_FAILED("00700", "Failed to Json serialize.", 500),
    JSON_DE_SERIALIZE_FAILED("00701", "Failed to Json deserialize.", 500),
    REQUEST_BODY_UNREADABLE("00702", "Unable to process the request.", 400),


    UNKNOWN_SERVER_ERROR("99999", "An unknown server error.", 500),


    ;
    private final String code;
    private final String message;
    private final int httpStatus;

    /**
     * Constructor for ErrorCode enum.
     *
     * @param code       Error Code
     * @param message    Error Message
     * @param httpStatus HTTP Status Code
     */
    ErrorCode(String code, String message, int httpStatus) {
        this.code = "S" + "SRV" + "ISS" + code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * Get the error code.
     *
     * @return Error Code
     */
    public static String getMessageByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return "Unknown error code: " + code;
    }
}
