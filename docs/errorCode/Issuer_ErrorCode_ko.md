---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Issuer Server 에러
==

- Date: 2025-05-30
- Version: v2.0.0

| 버전   | 일자       | 변경 내용       |
| ------ | ---------- | --------------- |
| v1.0.0 | 2024-08-20 | 최초 버전 |
| v2.0.0 | 2025-05-30 | 에러코드 추가 |

<div style="page-break-after: always;"></div>

# 목차
- [모델](#모델)
  - [오류 응답](#오류-응답)
    - [설명](#설명)
    - [선언](#선언)
    - [속성](#속성)
- [에러 코드](#에러-코드)
  - [1. Issuer 서버](#1-issuer-서버)
    - [1.1. Transaction(000xx)](#11-transaction000xx)
    - [1.2. DID(001xx)](#12-did001xx)
    - [1.3. VC(002xx)](#13-vc002xx)
    - [1.4. Holder(003xx)](#14-holder003xx)
    - [1.5. SDK(004xx)](#15-sdk004xx)
    - [1.6. Issuer(005xx)](#16-issuer005xx)
    - [1.7. B/C(006xx)](#17-bc006xx)
    - [1.8. etc.(007xx)](#18-etc007xx)
    - [1.9. ZKP(104xx)](#19-zkp104xx)
    - [1.10. Admin / Config(008xx)](#110-admin--config008xx)
    - [1.11. TAS(900xx)](#111-tas900xx)

# 모델
## 오류 응답

### 설명
```
Error struct for Issuer Backend. It has code and message pair.
Code starts with SSRVISS.
```

### 선언
```java
public class ErrorResponse {
    private final String code;
    private final String description;
}
```

### 속성

| 이름    | 타입   | 설명                               | **필수/선택** | **비고** |
| ------- | ------ | ---------------------------------- | ------------- | -------- |
| code    | String | Error code. It starts with SSRVISS | M             |          |
| message | String | Error description                  | M             |          |

<br>

# 에러 코드
## 1. Issuer 서버
### 1.1. Transaction(000xx)

| 에러 코드    | 에러 메시지                                                 | 설명 | 필요 조치 사항                                         |
| ------------ | ----------------------------------------------------------- | ---- | ------------------------------------------------------ |
| SSRVISS00001 | The transaction does not exist.                             | -    | Verify the transaction ID and try again.               |
| SSRVISS00002 | The transaction is not valid.                               | -    | Check the transaction details for validity.            |
| SSRVISS00003 | The transaction has expired.                                | -    | Initiate a new transaction.                            |
| SSRVISS00004 | The refId is not valid.                                     | -    | Confirm the refId.                                     |
| SSRVISS00005 | Failed to process the 'request-offer' API request.          | -    | Verify the API request payload and try again.          |
| SSRVISS00006 | Failed to process the 'inspect-propose-issue' API request.  | -    | Check the API request for valid input and retry.       |
| SSRVISS00007 | Failed to process the 'generate-issue-profile' API request. | -    | Ensure the profile data is correct and resubmit.       |
| SSRVISS00008 | Failed to process the 'issue-vc' API request.               | -    | Verify the input details for issuing VC and try again. |
| SSRVISS00009 | Failed to process the 'complete-vc' API request.            | -    | Review the completion parameters and retry.            |
| SSRVISS00010 | Failed to process the 'inspect-propose-revoke' API request. | -    | Confirm the revoke request details and resubmit.       |
| SSRVISS00011 | Failed to process the 'revoke-vc' API request.              | -    | Validate the VC revoke request and try again.          |
| SSRVISS00012 | Failed to process the 'complete-revoke-vc' API request.     | -    | Verify completion criteria for revocation and retry.   |
| SSRVISS00013 | Failed to process the 'issue-vc-result' API request.        | -    | Ensure the result parameters are valid and resubmit.   |
| SSRVISS00014 | Failed to process the 'issue-certificate-vc' API request.   | -    | Check certificate issuance details and try again.      |
| SSRVISS00015 | Failed to process the 'get-certificate-vc' API request.     | -    | Verify the certificate VC request and resubmit.        |
| SSRVISS00016 | Failed to process the 'update-vc-status' API request.       | -    | Ensure correct status update request and try again.    |


<br>

### 1.2. DID(001xx)

| 에러 코드    | 에러 메시지                      | 설명 | 필요 조치 사항                       |
| ------------ | -------------------------------- | ---- | ------------------------------------ |
| SSRVISS00100 | Failed to find DID Document.     | -    | Verify the DID and ensure it exists. |
| SSRVISS00101 | Invalid DID Document version.    | -    | Check the DID Document version.      |
| SSRVISS00102 | Invalid DID Document             | -    | Check the DID format.                |
| SSRVISS00103 | Failed to retrieve DID Document. | -    | Try fetching DID again.              |


<br>

### 1.3. VC(002xx)

| 에러 코드    | 에러 메시지                            | 설명 | 필요 조치 사항                                |
| ------------ | -------------------------------------- | ---- | --------------------------------------------- |
| SSRVISS00200 | The Offer does not exist.              | -    | Verify the offer ID and try again.            |
| SSRVISS00201 | The Offer has expired.                 | -    | Request a new offer or extend validity.       |
| SSRVISS00202 | The vcPlanId is not valid.             | -    | Check the vcPlanId for correctness.           |
| SSRVISS00203 | The profile id is not valid.           | -    | Validate the profile ID and retry.            |
| SSRVISS00204 | The profile issuer nonce is not valid. | -    | Ensure the issuer nonce is correct.           |
| SSRVISS00205 | Failed to Issue VC.                    | -    | Review issuance process and retry.            |
| SSRVISS00206 | The VC does not exist.                 | -    | Confirm the VC existence and try again.       |
| SSRVISS00207 | VC ID does not match.                  | -    | Verify the VC ID and correct it.              |
| SSRVISS00208 | Failed to parse Request VC.            | -    | Check the request format and retry.           |
| SSRVISS00209 | This VC is Revoked.                    | -    | Use a valid, non-revoked VC.                  |
| SSRVISS00210 | The issuer nonce is not valid.         | -    | Ensure the nonce matches the issuer.          |
| SSRVISS00211 | VC Schema name is not valid.           | -    | Verify and correct the schema name.           |
| SSRVISS00212 | Failed to generate VC                  | -    | Check the generation process and try again.   |
| SSRVISS00213 | Failed to parse VC Schema              | -    | Validate the schema format and retry parsing. |
| SSRVISS00214 | VC Schema not found for given ID.      | -    | Verify VC schema ID.                          |

<br>

### 1.4. Holder(003xx)

| 에러 코드    | 에러 메시지                | 설명 | 필요 조치 사항                             |
| ------------ | -------------------------- | ---- | ------------------------------------------ |
| SSRVISS00300 | The Holder does not exist. | -    | Verify holder information and retry.       |
| SSRVISS00301 | The User does not exist.   | -    | Confirm user details or create a new user. |
| SSRVISS00302 | The Holder is not valid.   | -    | Verify the Holder details and try again.   |

<br>

### 1.5. SDK(004xx)

| 에러 코드    | 에러 메시지                                     | 설명 | 필요 조치 사항                                         |
| ------------ | ----------------------------------------------- | ---- | ------------------------------------------------------ |
| SSRVISS00400 | Failed to encoding data.                        | -    | Check the encoding process and retry.                  |
| SSRVISS00401 | Failed to decoding data.                        | -    | Verify decoding logic and input data.                  |
| SSRVISS00402 | Failed to encrypt data.                         | -    | Review encryption parameters and retry.                |
| SSRVISS00403 | Failed to decrypt data.                         | -    | Ensure correct decryption key and retry.               |
| SSRVISS00404 | Failed to generate nonce.                       | -    | Retry nonce generation with valid inputs.              |
| SSRVISS00405 | Failed to generate initial vector.              | -    | Check the IV generation process.                       |
| SSRVISS00406 | Failed to generate session key.                 | -    | Review key generation logic and retry.                 |
| SSRVISS00407 | Failed to merge nonce and shared secret.        | -    | Verify merging process and inputs.                     |
| SSRVISS00408 | Failed to merge nonce.                          | -    | Ensure nonce integrity and retry.                      |
| SSRVISS00409 | Invalid symmetric cipher type.                  | -    | Use a valid symmetric cipher type.                     |
| SSRVISS00410 | Failed to generate key pair.                    | -    | Check key generation parameters.                       |
| SSRVISS00411 | Failed to uncompress public key.                | -    | Verify the key compression method.                     |
| SSRVISS00412 | Failed to compress public key.                  | -    | Review public key compression logic.                   |
| SSRVISS00413 | Failed to Signature verification.               | -    | Validate the signature and try again.                  |
| SSRVISS00415 | Failed to generate signature.                   | -    | Check the signature generation process.                |
| SSRVISS00416 | Failed to generate hash value.                  | -    | Ensure correct hashing algorithm.                      |
| SSRVISS00417 | Wallet is not registered.                       | -    | Register the wallet before proceeding.                 |
| SSRVISS00418 | Failed to connect wallet.                       | -    | Verify wallet connection details.                      |
| SSRVISS00419 | Failed to create wallet.                        | -    | Check wallet creation parameters.                      |
| SSRVISS00420 | Failed to get sign key ids                      | -    | Verify the key retrieval process and try again.        |
| SSRVISS00421 | Failed to get sign data                         | -    | Check the signing data request and retry.              |
| SSRVISS00422 | Failed to retrieve verification method.         | -    | Validate the verification method request and resubmit. |
| SSRVISS00423 | Failed to generate hash value.                  | -    | Verify the input data for hash generation and retry.   |
| SSRVISS00424 | Failed to get File wallet manager               | -    | Check wallet manager service.                          |
| SSRVISS00425 | Failed to create wallet: wallet already exists. | -    | Use a different wallet ID.                             |
| SSRVISS00426 | Invalid proof purpose.                          | -    | Verify proof purpose parameter.                        |
| SSRVISS00427 | Failed to generate keys: key already exists.    | -    | Avoid duplicate key creation.                          |


### 1.6. Issuer(005xx)

| 에러 코드    | 에러 메시지                    | 설명 | 필요 조치 사항                        |
| ------------ | ------------------------------ | ---- | ------------------------------------- |
| SSRVISS00501 | Certificate VC data not found. | -    | Verify certificate data availability. |

<br>

### 1.7. B/C(006xx)

| 에러 코드    | 에러 메시지                                        | 설명 | 필요 조치 사항                                        |
| ------------ | -------------------------------------------------- | ---- | ----------------------------------------------------- |
| SSRVISS00600 | Failed to initialize blockchain.                   | -    | Check blockchain initialization parameters and retry. |
| SSRVISS00601 | Failed to register DID Document on the blockchain. | -    | Verify DID Document and reattempt registration.       |
| SSRVISS00602 | Failed to retrieve DID document on the blockchain. | -    | Validate the retrieval request and try again.         |
| SSRVISS00603 | Failed to update DID Document on the blockchain.   | -    | Ensure the update data is correct and resubmit.       |
| SSRVISS00604 | Failed to register VC meta on the blockchain.      | -    | Confirm the VC meta details and retry registration.   |
| SSRVISS00605 | Failed to retrieve VC meta on the blockchain.      | -    | Check the retrieval parameters and try again.         |
| SSRVISS00606 | Failed to update VC status on the blockchain.      | -    | Verify the status update request and reattempt.       |

<br>

### 1.8. etc.(007xx)

| 에러 코드    | 에러 메시지                                               | 설명 | 필요 조치 사항                           |
| ------------ | --------------------------------------------------------- | ---- | ---------------------------------------- |
| SSRVISS00700 | Failed to Json serialize.                                 | -    | Check JSON structure and try again.      |
| SSRVISS00701 | Failed to Json deserialize.                               | -    | Check structure and try again.           |
| SSRVISS00702 | Unable to process the request.                            | -    | Review and correct the request format.   |
| SSRVISS00703 | Failed to serialize SchemaClaims object to JSON.          | -    | Check schema claims and retry.           |
| SSRVISS00704 | Failed to deserialize JSON string to SchemaClaims object. | -    | Verify JSON structure and retry.         |
| SSRVISS99999 | An unknown server error.                                  | -    | Investigate server logs and retry later. |

<br>

### 1.9. ZKP(104xx)

| 에러 코드    | 에러 메시지                                             | 설명 | 필요 조치 사항                         |
| ------------ | ------------------------------------------------------- | ---- | -------------------------------------- |
| SSRVISS10400 | ZKP Wallet is not registered.                           | -    | Register ZKP wallet.                   |
| SSRVISS10401 | Failed to connect ZKP wallet.                           | -    | Verify ZKP wallet connection.          |
| SSRVISS10402 | Failed to create ZKP wallet.                            | -    | Retry ZKP wallet creation.             |
| SSRVISS10403 | Failed to create ZKP wallet: ZKP wallet already exists. | -    | Check for existing ZKP wallet.         |
| SSRVISS10404 | Failed to Generate Correctness Proof.                   | -    | Verify correctness proof logic.        |
| SSRVISS10405 | Failed to Create Credential Offer.                      | -    | Check credential offer creation steps. |
| SSRVISS10406 | Failed to Create Attribute Value.                       | -    | Review ZKP attribute logic.            |
| SSRVISS10407 | Failed to Create Signature correctness proof.           | -    | Validate ZKP signature process.        |
| SSRVISS10408 | Failed to Create Credential Signature.                  | -    | Retry credential signature creation.   |
| SSRVISS10409 | Failed to Issued Credential.                            | -    | Review and retry issuance logic.       |

<br>

### 1.10. Admin / Config(008xx)

| 에러 코드    | 에러 메시지                                                                 | 설명 | 필요 조치 사항                                           |
| ------------ | --------------------------------------------------------------------------- | ---- | -------------------------------------------------------- |
| SSRVISS00800 | Failed to find admin: admin is not registered.                              | -    | Register or verify admin user.                           |
| SSRVISS00801 | Application config not found.                                               | -    | Check configuration setup.                               |
| SSRVISS00802 | Cannot delete namespace: it is referenced by a VC schema.                   | -    | Remove references before deletion.                       |
| SSRVISS00803 | Cannot delete vc schema: it is referenced by a Issue Profile.               | -    | Unlink schema from profiles.                             |
| SSRVISS00804 | Namespace not found for the given ID.                                       | -    | Verify namespace ID.                                     |
| SSRVISS00805 | Issuer info not found during initialization.                                | -    | Confirm issuer registration.                             |
| SSRVISS00806 | Issuer is already registered                                                | -    | Use different issuer or update info.                     |
| SSRVISS00807 | Failed to register Issuer DID Document: document is already requested.      | -    | Cancel or wait for existing request.                     |
| SSRVISS00808 | Failed to register Issuer DID Document: document is already registered.     | -    | Skip re-registration.                                    |
| SSRVISS00809 | Failed to generate DID document.                                            | -    | Check DID document generation logic.                     |
| SSRVISS00810 | Failed to register Issuer DID Document.                                     | -    | Retry DID registration.                                  |
| SSRVISS00811 | Failed to find Issuer DID Document: no registration request made.           | -    | Submit DID registration request.                         |
| SSRVISS00812 | Failed to process certificate VC: invalid JSON format.                      | -    | Fix certificate VC JSON format.                          |
| SSRVISS00813 | Failed to process the 'request-certificate-vc' API request.                 | -    | Check API request payload.                               |
| SSRVISS00814 | Failed to load key element.                                                 | -    | Check key storage and retry.                             |
| SSRVISS00815 | Failed to communicate with tas: unknown error occurred.                     | -    | Verify TAS server connectivity.                          |
| SSRVISS00816 | Failed to ping the URL.                                                     | -    | Check URL status.                                        |
| SSRVISS00817 | Failed to register admin: admin is already registered.                      | -    | Skip or update existing admin.                           |
| SSRVISS00818 | Failed to save ZKP Namespace.                                               | -    | Check DB connection and namespace structure.             |
| SSRVISS00819 | ZKP Namespace not found for the given ID.                                   | -    | Verify namespace ID exists before querying.              |
| SSRVISS00820 | Failed to retrieve ZKP Namespace.                                           | -    | Ensure namespace exists and is accessible.               |
| SSRVISS00821 | Failed to update ZKP Namespace.                                             | -    | Check input data and DB update logic.                    |
| SSRVISS00822 | Failed to delete ZKP Namespace.                                             | -    | Confirm deletable state and retry.                       |
| SSRVISS00823 | Failed to save ZKP Schema.                                                  | -    | Ensure schema is valid and DB save logic is correct.     |
| SSRVISS00824 | ZKP Attribute not found for the given ID.                                   | -    | Check the attribute ID or input source.                  |
| SSRVISS00825 | Failed to register ZKP Schema. (Blockchain or List Provider)                | -    | Check external system status and registration logic.     |
| SSRVISS00826 | Failed to register ZKP Schema: schema ID already exists.                    | -    | Use a unique schema ID when registering.                 |
| SSRVISS00827 | ZKP Schema not found for the given ID.                                      | -    | Verify schema ID before accessing.                       |
| SSRVISS00828 | Failed to register ZKP Credential Definition: alias already exists.         | -    | Use a different alias for registration.                  |
| SSRVISS00829 | Failed to generate ZKP Credential Definition.                               | -    | Check schema structure and generation logic.             |
| SSRVISS00830 | Failed to register ZKP Credential Definition. (Blockchain or List Provider) | -    | Verify external systems and credentials.                 |
| SSRVISS00831 | ZKP Credential Definition not found for the given ID.                       | -    | Check the definition ID and confirm registration status. |
| SSRVISS00832 | ZKP Credential schema is already in use.                                    | -    | Ensure the schema is not already registered or used.     |

<br>

### 1.11. TAS(900xx)

| 에러 코드    | 에러 메시지                                                     | 설명 | 필요 조치 사항                         |
| ------------ | --------------------------------------------------------------- | ---- | -------------------------------------- |
| SSRVISS90000 | Failed to process response: received unknown data from the Tas. | -    | Check TAS response format and content. |
