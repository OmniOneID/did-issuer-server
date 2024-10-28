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

Issuer Server Error
==

- Date: 2024-08-20
- Version: v1.0.0

| Version          | Date       | Changes                  |
| ---------------- | ---------- | ------------------------ |
| v1.0.0  | 2024-08-20 | Initial version          |

<div style="page-break-after: always;"></div>

# Table of Contents
- [Issuer Server Error](#issuer-server-error)
- [Table of Contents](#table-of-contents)
- [Model](#model)
  - [Error Response](#error-response)
    - [Description](#description)
    - [Declaration](#declaration)
    - [Property](#property)
- [Error Code](#error-code)
  - [1. Issuer Backend](#1-issuer-backend)
    - [1.1. Transaction(000xx)](#11-transaction000xx)
    - [1.2. DID(001xx)](#12-did001xx)
    - [1.3. VC(002xx)](#13-vc002xx)
    - [1.4. Holder(003xx)](#14-holder003xx)
    - [1.5. SDK(004xx)](#15-sdk004xx)
    - [1.6. Issuer(005xx)](#16-issuer005xx)
    - [1.7. B/C(006xx)](#17-bc006xx)
    - [1.8. etc.(007xx)](#18-etc007xx)

# Model
## Error Response

### Description
```
Error struct for Issuer Backend. It has code and message pair.
Code starts with SSRVISS.
```

### Declaration
```java
public class ErrorResponse {
    private final String code;
    private final String description;
}
```

### Property

| Name               | Type       | Description                            | **M/O** | **Note**              |
|--------------------|------------|----------------------------------------|---------|-----------------------|
| code               | String     | Error code. It starts with SSRVISS     |    M    |                       | 
| message            | String     | Error description                      |    M    |                       | 

<br>

# Error Code
## 1. Issuer Backend
### 1.1. Transaction(000xx)

| Error Code   | Error Message                                | Description      | Action Required                             |
|--------------|----------------------------------------------|------------------|---------------------------------------------|
| SSRVISS00000 | The transaction does not exist.              | -                | Verify the transaction ID and try again.    |
| SSRVISS00001 | The transaction is not valid.                | -                | Check the transaction details for validity. |
| SSRVISS00002 | The transaction has expired.                 | -                | Initiate a new transaction.                 |
| SSRVISS00003 | The refId is not valid.                      | -                | Confirm the refId.                          |
| SSRVISS00005 | Failed to process the 'request-offer' API request.               | -           | Verify the API request payload and try again.            |
| SSRVISS00006 | Failed to process the 'inspect-propose-issue' API request.       | -           | Check the API request for valid input and retry.         |
| SSRVISS00007 | Failed to process the 'generate-issue-profile' API request.      | -           | Ensure the profile data is correct and resubmit.         |
| SSRVISS00008 | Failed to process the 'issue-vc' API request.                    | -           | Verify the input details for issuing VC and try again.   |
| SSRVISS00009 | Failed to process the 'complete-vc' API request.                 | -           | Review the completion parameters and retry.              |
| SSRVISS00010 | Failed to process the 'inspect-propose-revoke' API request.      | -           | Confirm the revoke request details and resubmit.         |
| SSRVISS00011 | Failed to process the 'revoke-vc' API request.                   | -           | Validate the VC revoke request and try again.            |
| SSRVISS00012 | Failed to process the 'complete-revoke-vc' API request.          | -           | Verify completion criteria for revocation and retry.     |
| SSRVISS00013 | Failed to process the 'issue-vc-result' API request.             | -           | Ensure the result parameters are valid and resubmit.     |
| SSRVISS00014 | Failed to process the 'issue-certificate-vc' API request.        | -           | Check certificate issuance details and try again.        |
| SSRVISS00015 | Failed to process the 'get-certificate-vc' API request.          | -           | Verify the certificate VC request and resubmit.          |
| SSRVISS00016 | Failed to process the 'update-vc-status' API request.            | -           | Ensure correct status update request and try again.      |


<br>

### 1.2. DID(001xx)

| Error Code   | Error Message                                | Description      | Action Required                        |
|--------------|----------------------------------------------|------------------|----------------------------------------|
| SSRVISS00100 | Failed to find DID Document.                 | -                | Verify the DID and ensure it exists.   |
| SSRVISS00101 | Invalid DID Document version.                | -                | Check the DID Document version.        |


<br>

### 1.3. VC(002xx)

| Error Code   | Error Message                                | Description      | Action Required                         |
|--------------|----------------------------------------------|------------------|-----------------------------------------|
| SSRVISS00200 | The Offer does not exist.                    | -                | Verify the offer ID and try again.      |
| SSRVISS00201 | The Offer has expired.                       | -                | Request a new offer or extend validity. |
| SSRVISS00202 | The vcPlanId is not valid.                   | -                | Check the vcPlanId for correctness.     |
| SSRVISS00203 | The profile id is not valid.                 | -                | Validate the profile ID and retry.      |
| SSRVISS00204 | The profile issuer nonce is not valid.       | -                | Ensure the issuer nonce is correct.     |
| SSRVISS00205 | Failed to Issue VC.                          | -                | Review issuance process and retry.      |
| SSRVISS00206 | The VC does not exist.                       | -                | Confirm the VC existence and try again. |
| SSRVISS00207 | VC ID does not match.                        | -                | Verify the VC ID and correct it.        |
| SSRVISS00208 | Failed to parse Request VC.                  | -                | Check the request format and retry.     |
| SSRVISS00209 | This VC is Revoked.                          | -                | Use a valid, non-revoked VC.            |
| SSRVISS00210 | The issuer nonce is not valid.               | -                | Ensure the nonce matches the issuer.    |
| SSRVISS00211 | VC Schema name is not valid.                 | -                | Verify and correct the schema name.     |
| SSRVISS00212 | Failed to generate VC            | -           | Check the generation process and try again.  |
| SSRVISS00213 | Failed to parse VC Schema        | -           | Validate the schema format and retry parsing.|

<br>

### 1.4. Holder(003xx)

| Error Code   | Error Message                                | Description      | Action Required                            |
|--------------|----------------------------------------------|------------------|--------------------------------------------|
| SSRVISS00300 | The Holder does not exist.                   | -                | Verify holder information and retry.       |
| SSRVISS00301 | The User does not exist.                     | -                | Confirm user details or create a new user. |
| SSRVISS00302 | The Holder is not valid.                     | -                | Verify the Holder details and try again.   |

<br>

### 1.5. SDK(004xx)

| Error Code   | Error Message                                            | Description | Action Required                          |
|--------------|----------------------------------------------------------|-------------|------------------------------------------|
| SSRVISS00400 | Failed to encoding data.                                 | -           | Check the encoding process and retry.    |
| SSRVISS00401 | Failed to decoding data.                                 | -           | Verify decoding logic and input data.    |
| SSRVISS00402 | Failed to encrypt data.                                  | -           | Review encryption parameters and retry.  |
| SSRVISS00403 | Failed to decrypt data.                                  | -           | Ensure correct decryption key and retry. |
| SSRVISS00404 | Failed to generate nonce.                                | -           | Retry nonce generation with valid inputs.|
| SSRVISS00405 | Failed to generate initial vector.                       | -           | Check the IV generation process.         |
| SSRVISS00406 | Failed to generate session key.                          | -           | Review key generation logic and retry.   |
| SSRVISS00407 | Failed to merge nonce and shared secret.                 | -           | Verify merging process and inputs.       |
| SSRVISS00408 | Failed to merge nonce.                                   | -           | Ensure nonce integrity and retry.        |
| SSRVISS00409 | Invalid symmetric cipher type.                           | -           | Use a valid symmetric cipher type.       |
| SSRVISS00410 | Failed to generate key pair.                             | -           | Check key generation parameters.         |
| SSRVISS00411 | Failed to uncompress public key.                         | -           | Verify the key compression method.       |
| SSRVISS00412 | Failed to compress public key.                           | -           | Review public key compression logic.     |
| SSRVISS00413 | Failed to Signature verification.                        | -           | Validate the signature and try again.    |
| SSRVISS00415 | Failed to generate signature.                            | -           | Check the signature generation process.  |
| SSRVISS00416 | Failed to generate hash value.                           | -           | Ensure correct hashing algorithm.        |
| SSRVISS00417 | Wallet is not registered.                                | -           | Register the wallet before proceeding.   |
| SSRVISS00418 | Failed to connect wallet.                                | -           | Verify wallet connection details.        |
| SSRVISS00419 | Failed to create wallet.                                 | -           | Check wallet creation parameters.        |
| SSRVISS00420 | Failed to get sign key ids                               | -           | Verify the key retrieval process and try again.       | 
| SSRVISS00421 | Failed to get sign data                                  | -           | Check the signing data request and retry.             | 
| SSRVISS00422 | Failed to retrieve verification method.                  | -           | Validate the verification method request and resubmit.| 
| SSRVISS00423 | Failed to generate hash value.                           | -           | Verify the input data for hash generation and retry.  |

<br>

### 1.6. Issuer(005xx)

| Error Code   | Error Message                                           | Description | Action Required                          |
|--------------|---------------------------------------------------------|-------------|------------------------------------------|
| SSRVISS00501 | Certificate VC data not found.                          | -           | Verify certificate data availability.    |

<br>

### 1.7. B/C(006xx)

| Error Code   | Error Message                                        | Description | Action Required                                       |
|--------------|------------------------------------------------------|-------------|-------------------------------------------------------|
| SSRVISS00600 | Failed to initialize blockchain.                     | -           | Check blockchain initialization parameters and retry.  |
| SSRVISS00601 | Failed to register DID Document on the blockchain.    | -           | Verify DID Document and reattempt registration.        |
| SSRVISS00602 | Failed to retrieve DID document on the blockchain.    | -           | Validate the retrieval request and try again.          |
| SSRVISS00603 | Failed to update DID Document on the blockchain.      | -           | Ensure the update data is correct and resubmit.        |
| SSRVISS00604 | Failed to register VC meta on the blockchain.         | -           | Confirm the VC meta details and retry registration.    |
| SSRVISS00605 | Failed to retrieve VC meta on the blockchain.         | -           | Check the retrieval parameters and try again.          |
| SSRVISS00606 | Failed to update VC status on the blockchain.         | -           | Verify the status update request and reattempt.        |

<br>

### 1.8. etc.(007xx)

| Error Code   | Error Message                                | Description      | Action Required                          |
|--------------|----------------------------------------------|------------------|------------------------------------------|
| SSRVISS00700 | Failed to Json serialize.                    | -                | Check JSON structure and try again.      |
| SSRVISS00701 | Failed to Json deserialize.                  | -                | Check structure and try again.           |
| SSRVISS00702 | Unable to process the request.               | -                | Review and correct the request format.   |
| SSRVISS99999 | An unknown server error.                     | -                | Investigate server logs and retry later. |