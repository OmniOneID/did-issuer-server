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
    INVALID_DID_DOCUMENT("00102", "Invalid DID Document", 400),
    DID_DOCUMENT_RETRIEVAL_FAILED("00103", "Failed to retrieve DID Document.", 500),

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
    VC_SCHEMA_NOT_FOUND("00214", "VC Schema not found for given ID.", 400),



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
    FAILED_TO_GET_FILE_WALLET_MANAGER("00424", "Failed to get File wallet manager", 500),
    WALLET_ALREADY_EXISTS("00425", "Failed to create wallet: wallet already exists.", 500),
    INVALID_PROOF_PURPOSE("00426", "Invalid proof purpose.", 400),
    CRYPTO_KEY_PAIR_ALREADY_EXISTS("00427", "Failed to generate keys: key already exists.", 500),

    // ZKP 10400~10499 = ZKP Error
    ZKP_WALLET_INFO_NOT_FOUND("10400", "ZKP Wallet is not registered.", 400),
    ZKP_WALLET_CONNECT_FAILURE("10401", "Failed to connect ZKP wallet.", 500),
    ZKP_WALLET_CREATION_FAILURE("10402", "Failed to create ZKP wallet.", 500),
    ZKP_WALLET_ALREADY_EXISTS("10403", "Failed to create ZKP wallet: ZKP wallet already exists.", 500),

    FAILED_TO_GENERATE_CORRECTNESS_PROOF("10404", "Failed to Generate Correctness Proof.", 500),
    FAILED_TO_GENERATE_CREDENTIAL_OFFER("10405", "Failed to Create Credential Offer.", 500),

    FAILED_TO_GENERATE_ZKP_ATTRIBUTE_VALUE("10406", "Failed to Create Attribute Value.", 500),
    FAILED_TO_SIGNATURE_CORRECTNESS_PROOF("10407", "Failed to Create Signature correctness proof.", 500),
    FAILED_TO_CREDENTIAL_SIGNATURE("10407", "Failed to Create Credential Signature.", 500),
    FAILED_TO_ISSUE_CREDENTIAL("10407", "Failed to Issued Credential.", 500),

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
    JSON_SCHEMA_CLAIMS_SERIALIZE_FAILED("00703", "Failed to serialize SchemaClaims object to JSON.", 500),
    JSON_SCHEMA_CLAIMS_DESERIALIZE_FAILED("00704", "Failed to deserialize JSON string to SchemaClaims object.", 500),

    // 800~ 999 = admin
    ADMIN_INFO_NOT_FOUND("00800", "Failed to find admin: admin is not registered.", 400),
    APPLICATION_CONFIG_NOT_FOUND("00801", "Application config not found.", 400),
    NAMESPACE_DELETE_CONFLICT("00802", "Cannot delete namespace: it is referenced by a VC schema.", 400),
    VC_SCHEMA_DELETE_CONFLICT("00803", "Cannot delete vc schema: it is referenced by a Issue Profile.", 400),
    NAMESPACE_NOT_FOUND("00804", "Namespace not found for the given ID.", 400),
    ISSUER_INFO_NOT_FOUND("00805", "Issuer info not found during initialization.", 400),
    ISSUER_ALREADY_REGISTERED("00806", "Issuer is already registered", 400),
    ISSUER_DID_DOCUMENT_ALREADY_REQUESTED("00807", "Failed to register Issuer DID Document: document is already requested.", 400),
    ISSUER_DID_DOCUMENT_ALREADY_REGISTERED("00808", "Failed to register Issuer DID Document: document is already registered.", 400),
    FAILED_TO_GENERATE_DID_DOCUMENT("00809", "Failed to generate DID document.", 500),
    FAILED_TO_REGISTER_ISSUER_DID_DOCUMENT("00810","Failed to register Issuer DID Document.", 500),
    ISSUER_DID_DOCUMENT_NOT_FOUND("00811","Failed to find Issuer DID Document: o registration request has been made.", 400),
    INVALID_CERTIFICATE_VC_JSON_FORMAT("00812", "Failed to process certificate VC: invalid JSON format.", 500),
    FAILED_TO_REQUEST_CERTIFICATE_VC("00813","Failed to process the 'request-certificate-vc' API request.", 500),
    FAILED_TO_LOAD_KEY_ELEMENT("00814", "Failed to load key element.", 500),
    TAS_COMMUNICATION_ERROR("00815","Failed to communicate with tas: unknown error occurred.", 500),
    URL_PING_ERROR("00816", "Failed to ping the URL.", 400),
    ADMIN_ALREADY_EXISTS("00817", "Failed to register admin: admin is already registered.", 400),
    ZKP_NAMESPACE_SAVE_FAILED("00818", "Failed to save ZKP Namespace.", 500),
    ZKP_NAMESPACE_NOT_FOUND("00819", "ZKP Namespace not found for the given ID.", 400),
    ZKP_NAMESPACE_RETRIEVAL_FAILED("00820", "Failed to retrieve ZKP Namespace.", 500),
    ZKP_NAMESPACE_UPDATE_FAILED("00821", "Failed to update ZKP Namespace.", 500),
    ZKP_NAMESPACE_DELETE_FAILED("00822", "Failed to delete ZKP Namespace.", 500),
    ZKP_SCHEMA_SAVE_FAILED("00823", "Failed to save ZKP Schema.", 500),
    ZKP_ATTRIBUTE_NOT_FOUND("00824", "ZKP Attribute not found for the given ID.", 400),
    ZKP_SCHEMA_REGISTRATION_FAILED("00825", "Failed to register ZKP Schema. (Blockchain or List Provider)", 500),
    ZKP_SCHEMA_ID_ALREADY_EXISTS("00826", "Failed to register ZKP Schema: schema ID already exists.", 400),
    ZKP_SCHEMA_NOT_FOUND("00827", "ZKP Schema not found for the given ID.", 400),
    CREDENTIAL_DEFINITION_ALIAS_ALREADY_EXISTS("00828", "Failed to register ZKP Credential Definition: alias already exists.", 400),
    CREDENTIAL_DEFINITION_GENERATION_FAILED("00829", "Failed to generate ZKP Credential Definition.", 500),
    CREDENTIAL_DEFINITION_REGISTRATION_FAILED("00830", "Failed to register ZKP Credential Definition. (Blockchain or List Provider)", 500),
    ZKP_CREDENTIAL_DEFINITION_NOT_FOUND("00831", "ZKP Credential Definition not found for the given ID.", 400),

    TAS_UNKNOWN_RESPONSE("000900", "Failed to process response: received unknown data from the Tas.", 500),

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
