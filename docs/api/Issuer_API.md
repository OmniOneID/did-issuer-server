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

Issuer API
==

- Date: 2024-08-19
- Version: v1.0.0
  
Table of Contents
--

<!-- TOC tocDepth:2..3 chapterDepth:2..6 -->
  - [1. Overview](#1-overview)
  - [2. Terminology](#2-terminology)
  - [3. API List](#3-api-list)
    - [3.1. Sequential API](#31-sequential-api)
    - [3.2. Single Call API](#32-single-call-api)
  - [4. P210 - VC Issuance Protocol](#4-p210---vc-issuance-protocol)
    - [4.1. Request Offer](#41-request-offer)
    - [4.2. Inspect Propose Issue](#42-inspect-propose-issue)
    - [4.3. Generate Issue Profile](#43-generate-issue-profile)
    - [4.4. Issue VC](#44-issue-vc)
    - [4.5. Complete VC](#45-complete-vc)
    - [4.6. Issue VC Result](#46-issue-vc-result)
  - [5. P220 - VC Revocation Protocol](#5-p220---vc-revocation-protocol)
    - [5.1. Inspect Propose Revoke](#51-inspect-propose-revoke)
    - [5.2. Revoke VC](#52-revoke-vc)
    - [5.3. Complete Revoke](#53-complete-revoke)
  - [6. Single Call API](#6-single-call-api)
    - [6.1. Save User Data](#61-save-user-data)
    - [6.2. Save VC Data](#62-save-vc-data)
    - [6.3. Issue Certificate VC](#63-issue-certificate-vc)
    - [6.4. Get Certificate Vc](#64-get-certificate-vc)
    - [6.5. Get Vc Schema](#65-get-vc-schema)
    - [6.6. Update Vc Status](#66-update-vc-status)

## 1. Overview

This document defines the APIs provided by the Issuer Service.

![Workflow](images/workflow_issuer.svg)

- The above diagram shows the protocols and APIs that the Issuer Service provides or calls, with only Standard APIs displayed for readability.
- Each term is explained in Chapter 2, and the API list and call examples can be found from Chapter 3 onwards.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 2. Terminology
- Protocol
  - A collection of `Sequential APIs` that must be called in a predetermined order to perform a specific function. The API call sequence must be strictly followed, and incorrect sequencing may lead to unexpected results.
  - Protocols start with P and consist of 3 digits.
   - Example: P210 - VC Issuance Protocol
- Sequential API
  - A series of APIs that are called in a predetermined order to perform a specific function (protocol). Each API must be called sequentially, and incorrect sequencing may cause malfunction.
  - However, some protocols may have APIs with the same call sequence, in which case one API can be selected and called.
- Single Call API
  - APIs that can be called independently regardless of sequence, like general REST APIs.
- Standard API
  - APIs that are clearly defined in the API documentation and must be provided consistently across all implementations. Standard APIs ensure interoperability between systems and must operate according to predefined specifications.
- Non-Standard API
  - APIs that can be defined or customized differently according to the needs of each implementation. The non-standard APIs provided in this document are merely examples, and can be implemented differently for each implementation. In such cases, separate documentation for each implementation is required.
  - For example, VC information storage functionality may vary in implementation depending on the system, and non-standard APIs like `save-vc-data` can be redefined as needed by each implementation.

<div style="page-break-after: always; margin-top: 50px;"></div>

## 3. API List

### 3.1. Sequential API

#### 3.1.1. P210 - VC Issuance Protocol
| Seq | API                      | URL                                   | Description             | Standard API |
| --- | ------------------------ | ------------------------------------- | ----------------------- | ------------ |
| 1   | `request-offer`          | /api/v1/request-offer          | VC Issuance Offer Request (QR) | N       |
| 2   | `inspect-propose-issue`  | /api/v1/inspect-propose-issue  | VC Issuance Request Verification | Y       |
| 3   | `generate-issue-profile` | /api/v1/generate-issue-profile | Issue Profile Generation | Y       |
| 4   | `issue-vc`               | /api/v1/issue-vc               | VC Issuance | Y       |
| 5   | `complete-vc`            | /api/v1/complete-vc            | VC Issuance Completion | Y       |
| 6   | `issue-vc-result`        | /api/v1/issue-vc/result        | VC Issuance Result Verification | N       |

<div style="page-break-after: always; margin-top: 40px;"></div>

#### 3.1.2. P220 - VC Revocation Protocol
| Seq | API                      | URL                                      | Description       | Standard API |
| --- | ------------------------ | ---------------------------------------- | ----------------- | ------------ |
| 1   | `inspect-propose-revoke` | /api/v1/vc/inspect-propose-revoke | VC Revocation Request Verification | Y       |
| 2   | `revoke-vc`              | /api/v1/vc/revoke-vc                 | VC Revocation Request | Y       |
| 3   | `complete-revoke`        | /api/v1/vc/complete-revoke           | VC Revocation Completion | Y       |

<div style="page-break-after: always; margin-top: 40px;"></div>

### 3.2. Single Call API
| API                    | URL                           | Description          | Standard API |
| ---------------------- | ----------------------------- | -------------------- | ------------ |
| `save-user-data`       | /api/v1/user           | Save User Information | N       |
| `save-vc-data`         | /api/v1/vc             | Save VC Information | N       |
| `issue-certificate-vc` | /api/v1/certificate-vc | Entity Registration Request | N       |
| `get-certificate-vc`   | /api/v1/certificate-vc | Certificate of Registration Inquiry Request | N       |
| `get-vcschema`         | /api/v1/vc/vcschema    | VC Schema Inquiry | N       |
| `update-vc-status`     | /api/v1/vc/status      | VC Status Change | N       |

<div style="page-break-after: always; margin-top: 50px;"></div>

## 4. P210 - VC Issuance Protocol

| Seq. | API                    | Description        | Standard API |
| :--: | ---------------------- | ------------------ | ------------ |
|  1   | request-offer          | VC Issuance Offer Request | N       |
|  2   | inspect-propose-issue  | VC Issuance Request Verification  | Y       |
|  3   | generate-issue-profile | Issue Profile Generation | Y       |
|  4   | issue-vc               | VC Issuance            | Y       |
|  5   | complete-vc            | VC Issuance Completion       | Y       |
|  6   | issue-vc-result        | VC Issuance Result Verification  | N       |

### 4.1. Request Offer 

This is the process of requesting issuance session information for VC issuance. The Issuer already knows the user's information related to the VC to be issued and can provide issuance session information to the authorization app. This issuance session information is called Issue Offer, and users can start the issuance process through this information.

For example, in the case of a civil servant ID that can only be issued to employees of a specific institution, the employee's information is already registered, and the issuer can create and provide an issuance session based on this information.

| Item          | Description             | Remarks |
| ------------- | ----------------------- | ------- |
| Method        | `POST`                  |         |
| Path          | `/api/v1/request-offer` |         |
| Authorization | -                       |         |

#### 4.1.1. Request

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

```c#
def object RequestOffer: "Request Offer request message"
{    
   + vcPlanId     "vcPlanId"    : "VC plan id"
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.1.2. Response

**■ Process**
1. Retrieve VC Plan by vcPlanId
1. Generate offerId
1. Create IssueOfferPayload
1. Save by mapping offerId
   - issuer's did, vcPlanId, offerType

**■ Status 200 - Success**

```c#
def object _RequestOffer: "Retrieve KYC response message"
{    
   - uuid  "txId": "transaction id"
   + IssueOfferPayload  "issueOfferPayload": "issue offer payload" // Refer to data specification
}
```

**■ Status 400 - Client error**

| Code         | Description                     |
| ------------ | ------------------------------- |
| SSRVISS00202 | Non-existent `vcPlanId`. |

**■ Status 500 - Server error**

| Code         | Description                                   |
| ------------ | --------------------------------------------- |
| SSRVISS00005 | Failed to process 'request-offer' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.1.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/request-offer" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
{
   "vcPlanId": "vcplanid000000000001"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "issueOfferPayload" {
       "offerId":"99999999-9999-9999-9999-999999999999",
       "type":"IssueOffer",
       "vcPlanId":"vcplanid000000000001",
       "issuer":"did:omn:issuer",
       "validUntil":"2030-01-01T09:00:00Z"
   }
}

```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.2. Inspect Propose Issue

Verifies the VC issuance request.

| Item          | Description                     | Remarks |
| ------------- | ------------------------------- | ------- |
| Method        | `POST`                          |         |
| Path          | `/api/v1/inspect-propose-issue` |         |
| Authorization | -                               |         |

#### 4.2.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M210_InspectProposeIssue: "Inspect Propose Issue request message"
{
   //--- Common Part ---
   + messageId "id": "message id"

   //--- Data Part ---
   + vcPlanId "vcPlanId": "VC plan id"
   - did      "issuer"  : "issuer DID"
   - uuid     "offerId" : "VC offer id"
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.2.2. Response

Verifies the VC Plan specified in `vcPlanId` and checks if VC issuance is possible.
If issuance is possible, generates a reference number and responds.

**■ Process**

1. Verify input information validity and issuance possibility
1. `txId` = Generate transaction code
1. `refId` = Generate reference number
1. Save `txId`, `refId`

**■ Status 200 - Success**

```c#
def object _M210_InspectProposeIssue: "Inspect Propose Issue response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"

   //--- Data Part ---
   + refId "refId": "reference number"
}
```

**■ Status 400 - Client error**

| Code         | Description            |
| :----------: | ---------------------- |
| SSRVISS00200 | Non-existent `offerId`.      |
| SSRVISS00201 | Expired `offerId`. |
| SSRVISS00202 | Non-existent `vcPlanId`.     |

**■ Status 500 - Server error**

| Code         | Description        |
| :----------: | -------------------|
| SSRVISS00006 | Failed to process 'inspect-propose-issue' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.2.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/inspect-propose-issue" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
   "id":"20241023105126512000e3fae231",
   "vcPlanId":"vcplanid000000000001",
   "issuer":"did:omn:issuer",
   "offerId":"99999999-9999-9999-9999-999999999999"
}

```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "txId":"99999999-9999-9999-9999-999999999999",
   "refId":"99999999999999999999"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.3. Generate Issue Profile

Creates and responds with an Issue Profile.

| Item          | Description                      | Remarks |
| ------------- | -------------------------------- | ------- |
| Method        | `POST`                           |         |
| Path          | `/api/v1/generate-issue-profile` |         |
| Authorization | -                                |         |

#### 4.3.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M210_GenerateIssueProfile: "Generate Issue Profile request message"
{
   //--- Common Part ---
   + messageId "id"  : "message id"
   + uuid      "txId": "transaction id"

   //--- Data Part ---
   + object "holder": "holder information"
   {
       + did      "did": "holder DID"
       - personId "pii": "holder PII"
   }
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.3.2. Response

If the TAS has performed identity verification in advance, PII may be received.
In the case of issuance initiation by the issuer, holder personal information (email address, user account, etc.) that the issuer created in advance can be verified using the `M210_InspectProposeIssue.offerId` value.

**■ Process**

1. Verify transaction code
1. `profile` = Create IssueProfile and attach signature

**■ Status 200 - Success**

```c#
def object _M210_GenerateIssueProfile: "Generate Issue Profile response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"

   //--- Data Part ---
   + IssueProfile "profile": "issue profile"
}
```

**■ Status 400 - Client error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00001 | Non-existent `Transaction`. |
| SSRVISS00002 | Invalid `Transaction`. |
| SSRVISS00003 | Expired `Transaction`. |
| SSRVISS00300 | Non-existent `Holder`. |

**■ Status 500 - Server error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00007 | Failed to process 'generate-issue-profile' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.3.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/generate-issue-profile" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
   "id":"did:example:123456789abcdefghi",
   "txId":"99999999-9999-9999-9999-999999999999",
   "holder":{
       "did":"did:omn:example123",
       "pii":"pii"
   }
}

```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "txId":"99999999-9999-9999-9999-999999999999",
 "profile":{
   "id":"4d2c9387-7239-43c0-b829-c899d4aeac19",
   "type":"IssueProfile",
   "title":"National ID",
   "description":"National ID",
   "encoding":"UTF-8",
   "language":"ko",
   "profile":{
     "issuer":{
       "did":"did:omn:issuer",
       "certVcRef":"http://127.0.0.1:8091/issuer/api/v1/certificate-vc",
       "name":"issuer"
     },
     "credentialSchema":{
       "id":"http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=national_id",
       "type":"OsdSchemaCredential"
     },
     "process":{
       "endpoints":[
         "http://127.0.0.1:8091/issuer"],
       "reqE2e":{
         "nonce":"mbRz+NJ7fEZEyFiAGIcfktQ",
         "curve":"Secp256r1",
         "publicKey":"mA+1jfCC06BtbLwUkkAAsiU46i4GWz17SWnaME4yx7g2c",
         "cipher":"AES-256-CBC",
         "padding":"PKCS5"
       },
       "issuerNonce":"mbRz+NJ7fEZEyFiAGIcfktQ"
     }
   },
   "proof":{
     "type":"Secp256r1Signature2018",
     "created":"2024-08-29T10:36:10.879887Z",
     "verificationMethod":"did:omn:issuer#assert",
     "proofPurpose":"assertionMethod",
     "proofValue":"mIGTSOOl3ij...njfvPZBkJc0"
   }
 }
}

```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.4. Issue VC

Issues a VC, encrypts it with an E2E key, and responds.

| Item          | Description        | Remarks |
| ------------- | ------------------ | ------- |
| Method        | `POST`             |         |
| Path          | `/api/v1/issue-vc` |         |
| Authorization | -                  |         |

#### 4.4.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M210_IssueVc: "Issue VC request message"
{
   //--- Common Part ---
   + messageId "id"  : "message id"
   + uuid      "txId": "transaction id"

   //--- Data Part ---
   + AccE2e    "accE2e"  : "E2E acceptance information"
   + multibase "encReqVc": "multibase(enc((ReqVc)reqVc))"
}
```

- `~/accE2e`: E2E acceptance information corresponding to `IssueProfile:~/profile/process/reqE2e`
- `~/encReqVc`: VC issuance request information encrypted with E2E key
   - `refId`: `_M210_ProposeIssueVc:~/refId`
   - `profile`
       - `id`: `IssueProfile:~/id` 
       - `issuerNonce`: `IssueProfile:~/profile/process/issuerNonce` 

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.4.2. Response

Uses E2E acceptance information to generate an E2E encryption key and decrypts the issuance request information.
After verifying that the content in the issuance request information matches the information provided by the issuer, issues the VC if there are no issues.
The issued VC is encrypted with the E2E key and responded.

**■ Process**

1. Verify transaction code
1. (If present) Verify `accE2e.proof` signature
1. Perform E2E ECDH
   - `e2eKey` = Generate encryption key
   - `iv` = accE2e.iv
1. Decrypt `encReqVc`
   - reqVc = dec(debase(encReqVc), e2eKey, iv, padding)
1. Verify `reqVc` content
   - Verify `refId` match
   - Verify `profile/id` match
   - Verify `profile/issuerNonce` match
1. `vc` = Issue or reissue VC
1. Encrypt VC
   - `iv` = Generate IV
   - `encVc` = multibase(enc(vc, e2eKey, iv, padding))
1. Save VC id

**■ Status 200 - Success**

```c#
def object _M210_IssueVc: "Issue VC response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"

   //--- Data Part ---
   + object "e2e": "E2E encryption information"
   {
       + multibase "iv"   : "E2E encryption/decryption IV", byte_length(16)
       + multibase "encVc": "multibase(enc(vc))"
   }
}
```

**■ Status 400 - Client error**

| Code | Description        |
| :--: | ------------------ |
| SSRVISS00001 | Non-existent `Transaction`. |
| SSRVISS00002 | Invalid `Transaction`. |
| SSRVISS00003 | Expired `Transaction`. |
| SSRVISS00004 | Invalid `refId`.       |
| SSRVISS00203 | Invalid `profileId`.  |
| SSRVISS00204 | Invalid `issuerNonce`.  |
| SSRVISS00413 | Failed to verify `AccE2E` signature. |

**■ Status 500 - Server error**

| Code | Description  |
| :--: | ------------ |
| SSRVISS00008 | Failed to process 'issue-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.4.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/issue-vc" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
   "txId":"99999999-9999-9999-9999-999999999999",
   "accE2e":{
       "publicKey":"mAvuqNcA0akRCgC5anv6fTQFstQynq2WZgYg/9Eh0QkAy",
       "iv":"u9Mytc_E57cDAaAIIuCqfhw"
   },
   "encReqVc":"mYMh5+wqo+sFh...UcoJ/g"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "txId":"99999999-9999-9999-9999-999999999999",
 "e2e":{
   "iv":"mwZPbY3+4RBYwwzuLOM6AFA",
   "encVc":"mRK5CJoYbFzmvGm8VVv...Bde0U"
 }
}

```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.5. Complete VC

Completes the VC issuance.

| Item          | Description           | Remarks |
| ------------- | --------------------- | ------- |
| Method        | `POST`                |         |
| Path          | `/api/v1/complete-vc` |         |
| Authorization | -                     |         |

#### 4.5.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M210_CompleteVC: "Complete VC request message"
{
   //--- Common Part ---
   + messageId "id"  : "message id"
   + uuid      "txId": "transaction id"

   //--- Data Part ---
   + uuid "vcId": "VC id"
}
```

- `~/vcId`: VC id issued immediately before

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.5.2. Response

Verifies if the VC id matches and terminates the issuance transaction.

**■ Process**

1. Verify transaction code
1. Verify `vcId` match
1. Discard transaction code and reference number

**■ Status 200 - Success**

```c#
def object _M210_CompleteVC: "Complete VC response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"
}
```

**■ Status 400 - Client error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00001 | Non-existent `Transaction`. |
| SSRVISS00002 | Invalid `Transaction`. |
| SSRVISS00003 | Expired `Transaction`. |
| SSRVISS00207 | Does not match the issued `vcId`. |


**■ Status 500 - Server error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00009 | Failed to process 'complete-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.5.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/complete-vc" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
 "id":"did:example:123456789abcdefghi",
 "txId":"99999999-9999-9999-9999-999999999999",
 "vcId":"vcid000000000001"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "txId":"99999999-9999-9999-9999-999999999999"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 4.6. Issue VC Result

Verifies the VC issuance result.

If Issuer Legacy has initiated VC issuance, even if the Holder successfully receives the VC, Issuer Legacy cannot automatically know the result. Therefore, Issuer Legacy can verify the VC issuance result through this API.

| Item          | Description                      | Remarks |
| ------------- | -------------------------------- | ------- |
| Method        | `GET`                            |         |
| Path          | `/issuer/api/v1/issue-vc/result` |         |
| Authorization | -                                |         |

#### 4.6.1. Request

**■ Path Parameters**

N/A

**■ Query Parameters**

| name        | Description   | Remarks |
| ----------- | ------------- | ------- |
| + `offerId` | `Issuance Offer id` |         |

**■ HTTP Body**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.6.2. Response

**■ Process**
1. Retrieve VC issuance result by offerId

**■ Status 200 - Success**

```c#
def object _GetVcPlan: "Get VC Plan response message"
{
   + uuid  "txId": "transaction id"
   + uuid  "offerId": "issuance offer id" 
   + bool "result": "issuance result"
}
```

**■ Status 400 - Client error**

| Code | Description        |
|------|--------------------|
| SSRVISS00200 | Non-existent offerId. |

**■ Status 500 - Server error**

| Code | Description |
| ---- | ----------- |
| SSRVISS00013 | Failed to process 'issue-vc-result' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 4.6.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/issuer/api/v1/issue-vc/result?offerId=99999999-9999-9999-9999-999999999999"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "txId":"99999999-9999-9999-9999-999999999999",
 "offerId":"99999999-9999-9999-9999-999999999999",
 "result":true
}
```

<div style="page-break-after: always; margin-top: 50px;"></div>

## 5. P220 - VC Revocation Protocol

| Seq. | API                    | Description       | Standard API |
| :--: | ---------------------- | ----------------- | ------------ |
|  1   | inspect-propose-revoke | VC Revocation Request Verification | Y |
|  2   | revoke-vc              | VC Revocation Request      | Y |
|  3   | complete-revoke        | VC Revocation Completion      | Y |

### 5.1. Inspect Propose Revoke

Verifies the VC revocation request.

| Item          | Description                      | Remarks |
| ------------- | -------------------------------- | ------- |
| Method        | `POST`                           |         |
| Path          | `/api/v1/inspect-propose-revoke` |         |
| Authorization | -                                |         |

#### 5.1.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M220_InspectProposeRevoke: "Inspect Propose Revoke request message"
{
   //--- Common Part ---
   + messageId "id": "message id"

   //--- Data Part ---
   + vcId "vcId": "VC id"
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.1.2. Response

After verifying that the VC specified in `vcId` exists, checks if VC revocation is possible.
If revocation is possible, responds with signature request information to confirm that the user has requested revocation.

**■ Process**

1. `txId` = Generate transaction code
1. Verify `vcId` validity and status
   - A VC registered with `vcId` must exist
   - Current status must not be `REVOKED`
1. `issuerNonce` = Generate 16-byte nonce for user signature verification
1. Save `txId`, `issuerNonce`

**■ Status 200 - Success**

```c#
def object _M220_InspectProposeRevoke: "Inspect Propose Revoke response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"

   //--- Data Part ---
   + multibase        "issuerNonce": "issuer nonce", byte_length(16)
   + VERIFY_AUTH_TYPE "authType"   : "authentication method"
}
```

- `issuerNonce`: nonce to be used when creating signatures for user authentication
- `authType`: authentication method
   - Specifies authentication method for `ReqRevokeVc` signature
   - Confirms that it is a user's revocation request and requests signature for future non-repudiation purposes
   - "No authentication"(1) cannot be used

**■ Status 400 - Client error**

| Code | Description        |
| :--: | ------------------ |
| SSRVISS00206 | Non-existent `vcId`. |
| SSRVISS00209 | Already revoked `VC`. |

**■ Status 500 - Server error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00010 | Failed to process 'inspect-propose-revoke' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.1.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/inspect-propose-revoke" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
   "id":"20240904161842958000c9ca1908",
   "vcId":"88c2d770-dd40-4209-81ee-53108d365d1d"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0",
 "issuerNonce":"ms8pZ1pDN6vCxGSiwBeyOwQ",
 "authType":6
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.2. Revoke VC

Revokes the VC.

| Item          | Description         | Remarks |
| ------------- | ------------------- | ------- |
| Method        | `POST`              |         |
| Path          | `/api/v1/revoke-vc` |         |
| Authorization | -                   |         |

#### 5.2.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M220_RevokeVc: "Revoke VC request message"
{
   //--- Common Part ---
   + messageId "id"  : "message id"
   + uuid      "txId": "transaction id"

   //--- Data Part ---
   + ReqRevokeVc "request": "VC revocation request information"
}
```

- `~/request`: VC revocation request information
   - `vcId`: Must be the same as `M220_InspectProposeIssue:~/vcId`
   - `issuerNonce`: Must be the same as `_M220_InspectProposeIssue:~/issuerNonce`
   - `proof` or `proofs`: user signature
       - Signature conforming to `_M220_InspectProposeIssue:~/authType`

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.2.2. Response

Verifies that the content in the revocation request information matches the information provided by the issuer, validates the signature, and revokes the VC if there are no issues.

**■ Process**

1. Verify transaction code
1. Verify `request` content
   - Verify `vcId` match
   - Verify `issuerNonce` match
   - `proof` or `proofs`
       - Verify if it matches the requested authentication method
       - Verify if the signer's DID matches the holder DID of the VC to be revoked
       - Verify signature
1. Save the following information for future non-repudiation
   - `vcId`, `txid`, `request`
1. Revoke VC

**■ Status 200 - Success**

```c#
def object _M220_RevokeVc: "Revoke VC response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"
}
```

**■ Status 400 - Client error**

| Code | Description                   |
| :--: | ----------------------------- |
| SSRVISS00001 | Non-existent `Transaction`. |
| SSRVISS00002 | Invalid `Transaction`. |
| SSRVISS00003 | Expired `Transaction`. |
| SSRVISS00207 | Does not match the `vcId` requested in 'Inspect Propose Revoke'. |
| SSRVISS00210 | Invalid `issuerNonce`. |
| SSRVISS00302 | Mismatch between requester and VC holder DID. |
| SSRVISS00413 | Failed to verify Holder signature. |

**■ Status 500 - Server error**

| Code | Description  |
| :--: | ------------ |
| SSRVISS00011 | Failed to process 'revoke-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.2.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/revoke-vc" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
 "id":"20240904161842958000c9ca1908",
 "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0",
 "request":{
   "proof":{
     "type":"Secp256r1Signature2018",
     "created":"2024-09-04T16:18:44.472836Z",
     "verificationMethod":"did:omn:issuer?versionId=1#assert",
     "proofPurpose":"assertionMethod",
     "proofValue":"mIEAVe0v5Bvtw6qXz.../ix4rU"
   },
   "vcId":"88c2d770-dd40-4209-81ee-53108d365d1d",
   "issuerNonce":"ms8pZ1pDN6vCxGSiwBeyOwQ"
 }
}

```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 5.3. Complete Revoke

Completes the VC issuance.

| Item          | Description               | Remarks |
| ------------- | ------------------------- | ------- |
| Method        | `POST`                    |         |
| Path          | `/api/v1/complete-revoke` |         |
| Authorization | -                         |         |

#### 5.3.1. Request

**■ Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ Body**

```c#
def object M220_CompleteRevoke: "Complete Revoke request message"
{
   //--- Common Part ---
   + messageId "id"  : "message id"
   + uuid      "txId": "transaction id"
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.3.2. Response

**■ Process**

1. Verify transaction code
2. Discard transaction code

**■ Status 200 - Success**

```c#
def object _M220_CompleteRevoke: "Complete Revoke response message"
{    
   //--- Common Part ---
   + uuid "txId": "transaction id"
}
```

**■ Status 400 - Client error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00001 | Non-existent `Transaction`. |
| SSRVISS00002 | Invalid `Transaction`. |
| SSRVISS00003 | Expired `Transaction`. |

**■ Status 500 - Server error**

| Code | Description |
| :--: | ----------- |
| SSRVISS00012 | Failed to process 'complete-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 5.3.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/complete-revoke" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
 "id":"20240904161842958000c9ca1908",
 "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0"
}
```

<div style="page-break-after: always; margin-top: 50px;"></div>

## 6. Single Call API

Single call APIs are independent APIs that perform specific functions.
Therefore, they are not sequential APIs (aka protocols) that must be called in order, so they are not assigned protocol numbers.
The list of single call APIs provided by the Issuer Service is shown in the table below.

| API                    | URL                           | Description          | Standard API |
| ---------------------- | ----------------------------- | -------------------- | ------------ |
| `save-user-data`       | /api/v1/user           | Save User Information     | N       |
| `save-vc-data`         | /api/v1/vc             | Save VC Information         | N       |
| `issue-certificate-vc` | /api/v1/certificate-vc | Entity Registration Request     | N       |
| `get-certificate-vc`   | /api/v1/certificate-vc | Certificate of Registration Inquiry Request | N       |
| `get-vcschema`         | /api/v1/vc/vcschema    | VC Schema Inquiry       | N       |
| `update-vc-status`     | /api/v1/vc/status      | VC Status Change         | N       |

■ Authorization

Protocols include APIs that 'verify the caller's calling authority' (authorization).
The single call APIs in the above list do not define authorization, but
the following approaches are being considered for future addition.

- Option 1) Issue tokens that can be used for a certain period after verifying `AttestedAppInfo` information signed by the authorization app operator
   - Attach TAS-issued token in header when calling single API
   - Separate token management API required
- Option 2) Authorization app operator issues tokens to authorization app and TAS requests token verification from authorization app operator
   - Attach authorization app operator-issued token in header when calling single API
   - Authorization app operator needs to implement functionality to issue and verify tokens

### 6.1. Save User Data
Saves user information including PII (Personally Identifiable Information), and also includes data required for VC issuance.

The Issuer may possess information such as PII that can identify users.
Therefore, this API must be called to allow the Issuer to save user information through demo sites, etc.

What information is saved depends on the VC to be issued to the user, but PII must be saved as a unique value.
However, note that default values are set for all request parameters for testing convenience.

| Item          | Description           | Remarks |
| ------------- | --------------------- | ------- |
| Method        | `POST`                |         |
| Path          | `/api/v1/user` |         |
| Authorization | -                     |         |

#### 6.1.1. Request

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

```c#
def object SaveUserData: "Save User Data request message"
{    
   - string "firstname": "user's first name", default("TE")
   - string "lastname": "user's last name", default("ST")
   - string "birthdate": "user's birth date", default("2024-01-01")
   - string "address": "user's address", default("TEST")
   - string "licenseNum": "user's license number", default("123123-123123")
   - string "issueDate": "license issue date", default("2024-01-01")
   + string "pii": "user's PII", default("TEST")
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.1.2. Response

**■ Process**
1. Retrieve user information by pii
1. If user information does not exist, load new information into DB; if it exists, update the information.

**■ Status 200 - Success**

```c#
def object _SaveUserData: "Save User Data response message"
{    
}
```

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.1.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/user" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
 "firstname": "user",
 "lastname": "user",
 "birthdate": "2024-01-01",
 "address": "Korea, South",
 "licenseNum": "12-123456-12",
 "issueDate": "2024-01-01",
 "pii": "12345abcde5678fghij"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 6.2. Save VC Data

Saves user information including DID, and also includes data required for VC issuance.

The Issuer may possess information such as DID that can identify users.
Therefore, this API must be called to allow the Issuer to save user information through demo sites, etc.

What information is saved depends on the VC to be issued to the user, but DID must be saved as a unique value.
However, note that default values are set for all request parameters for testing ease.

| Item          | Description  | Remarks |
| ------------- | ------------ | ------- |
| Method        | `POST`       |         |
| Path          | `/api/v1/vc` |         |
| Authorization | -            |         |

#### 6.2.1. Request

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

```c#
def object SaveVcData: "Save Vc Data request message"
{    
   + did "did": "user's DID", default("did:omn:test")
   - string "firstname": "user's first name", default("TE")
   - string "lastname": "user's last name", default("ST")
   - string "birthdate": "user's birth date", default("2024-01-01")
   - string "address": "user's address", default("TEST")
   - string "licenseNum": "user's license number", default("123123-123123"
   - string "issueDate": "license issue date", default("2024-01-01")
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.2.2. Response

**■ Process**
1. Retrieve user information by did
1. If user information does not exist, load new information into DB; if it exists, update the information.

**■ Status 200 - Success**

```c#
def object _SaveVcData: "Save VC Data response message"
{    
}
```

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.2.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/vc" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
 "did": "did:omn:user1",
 "firstname": "user",
 "lastname": "user",
 "birthdate": "2024-01-01",
 "address": "Korea, South",
 "licenseNum": "12-123456-12",
 "issueDate": "2024-01-01",
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 6.3. Issue Certificate VC

Requests the issuance of a certificate of registration.

The Issuer's DID Document must already be registered in the repository (e.g., blockchain) through the TAS administrator.
This API sequentially calls the TAS's P120 protocol APIs to obtain the certificate of registration.

| Item          | Description              | Remarks |
| ------------- | ------------------------ | ------- |
| Method        | `POST`                   |         |
| Path          | `/api/v1/certificate-vc` |         |
| Authorization | -                        |         |

#### 6.3.1. Request

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

```c#
def object IssueCertificateVc: "Issue Certificate VC request message"
{    
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.3.2. Response

**■ Process**
1. Sequentially call TAS's P120 protocol APIs
1. Save the issued certificate of registration to DB

**■ Status 200 - Success**

```c#
def object _IssueCertificateVc: "Issue Certificate VC response message"
{    
}
```

**■ Status 400 - Client error**

N/A

**■ Status 500 - Server error**

| Code         | Description                                          |
| ------------ | ---------------------------------------------------- |
| SSRVTRAXXXXX | Please refer to TAS_API                               |
| SSRVISS00014 | Failed to process 'issue-certificate-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.3.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/certificate-vc" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 6.4. Get Certificate Vc

Retrieves the certificate of registration.

| Item          | Description              | Remarks |
| ------------- | ------------------------ | ------- |
| Method        | `GET`                    |         |
| Path          | `/api/v1/certificate-vc` |         |
| Authorization | -                        |         |

#### 6.4.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |     

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.4.2. Response

**■ Process**
1. Retrieve certificate of registration

**■ Status 200 - Success**

```c#
def object _GetCertificateVc: "Get Certificate VC response message"
{
   @spread(Vc)  // Refer to data specification
}
```

**■ Status 400 - Client error**

| Code | Description        |
|------|--------------------|
| SSRVISS00501 | Certificate VC does not exist. |

**■ Status 500 - Server error**

| Code | Description             |
|------|-------------------------|
| SSRVISS00015 | Failed to process 'get-certificate-vc' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.4.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/issuer/api/v1/certificate-vc"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "@context": [
   "https://www.w3.org/ns/credentials/v2"
 ],
 "credentialSchema": {
   "id": "http://127.0.0.1:8090/tas/api/v1/vc-schema?name=certificate",
   "type": "OsdSchemaCredential"
 },
 "credentialSubject": {
   "claims": [
     {
       "caption": "subject",
       "code": "org.opendid.v1.subject",
       "format": "plain",
       "hideValue": false,
       "type": "text",
       "value": "o=issuer"
     },
     {
       "caption": "role",
       "code": "org.opendid.v1.role",
       "format": "plain",
       "hideValue": false,
       "type": "text",
       "value": "Issuer"
     }
   ],
   "id": "did:omn:issuer"
 },
 "encoding": "UTF-8",
 "evidence": [
   {
     "attribute": {
       "licenseNumber": "1234567890"
     },
     "documentPresence": "Physical",
     "evidenceDocument": "BusinessLicense",
     "subjectPresence": "Physical",
     "type": "DocumentVerification",
     "verifier": "did:omn:tas"
   }
 ],
 "formatVersion": "1.0",
 "id": "0e1bb025-9c6b-4fe1-af02-d831c05bc412",
 "issuanceDate": "2024-10-07T09:48:47Z",
 "issuer": {
   "id": "did:omn:tas",
   "name": "raonsecure"
 },
 "language": "ko",
 "proof": {
   "created": "2024-10-07T09:48:47Z",
   "proofPurpose": "assertionMethod",
   "proofValue": "mILCenKaJat...45asiivnCRVDao",
   "proofValueList": [
     "mH0RNBKmf+LK...7RDi7gia7PkawBTjFpXeR4",
     "mH1UhQZiJKLq...9otKSCmpqkM0FU"
   ],
   "type": "Secp256r1Signature2018",
   "verificationMethod": "did:omn:tas?versionId=1#assert"
 },
 "type": [
   "VerifiableCredential",
   "CertificateVC"
 ],
 "validFrom": "2024-10-07T09:48:47Z",
 "validUntil": "2025-10-07T09:48:47Z"
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 6.5. Get Vc Schema

Retrieves the VC Schema.

| Item          | Description           | Remarks |
| ------------- | --------------------- | ------- |
| Method        | `GET`                 |         |
| Path          | `/api/v1/vc/vcschema` |         |
| Authorization | -                     |         |

#### 6.5.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |      


**■ Path Parameters**

N/A

**■ Query Parameters**

| name     | Description      | Remarks |
| -------- | ---------------- | ------- |
| + `name` | `VC Schema name` |         |


**■ HTTP Body**

N/A

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.5.2. Response

**■ Process**
1. Retrieve VC Schema by name

**■ Status 200 - Success**

```c#
def object _GetVcSchema: "Get VC Schema response message"
{
   @spread(VcSchema)  // Refer to data specification
}
```

**■ Status 400 - Client error**

| Code         | Description                         |
| ------------ | ----------------------------------- |
| SSRVISS00211 | Invalid vc schema name. |

**■ Status 500 - Server error**

N/A 

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.5.3. Example

**■ Request**

```shell
curl -v -X GET "http://${Host}:${Port}/issuer/api/v1/vc/vcschema?name=mdl"
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
 "@id": "http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=mdl",
 "@schema": "https://opendid.org/schema/vc.osd",
 "title": "OpenDID Mobile Driver License",
 "description": "VC-formatted OpenDID Mobile License Driver.",
 "metadata": {
   "language": "ko",
   "formatVersion": "1.0"
 },
 "credentialSubject": {
   "claims": [
     {
       "namespace": {
         "id": "org.iso.18013.5",
         "name": "ISO/IEC 18013-5:2021 - Personal identification",
         "ref": "https://www.iso.org/standard/69084.html"
       },
       "items": [
         {
           "id": "family_name",
           "caption": "Family Name",
           "type": "text",
           "format": "plain"
         },
         {
           "id": "given_name",
           "caption": "Given Name",
           "type": "text",
           "format": "plain"
         },
         {
           "id": "birth_date",
           "caption": "Birth date",
           "type": "text",
           "format": "plain"
         },
         {
           "id": "address",
           "caption": "Address",
           "type": "text",
           "format": "plain"
         },
         {
           "id": "document_number",
           "caption": "Document Number",
           "type": "text",
           "format": "plain"
         },
         {
           "id": "issue_date",
           "caption": "Issue Date",
           "type": "text",
           "format": "plain"
         }
       ]
     },
     {
       "namespace": {
         "id": "org.opendid.v1",
         "name": "OpenDID v1",
         "ref": "https://opendid.org/schema/v1/claim"
       },
       "items": [
         {
           "id": "pii",
           "caption": "PII",
           "type": "text",
           "format": "plain",
           "hideValue": false,
           "description": "Personal identifier that differs by country; Korea uses CI"
         }
       ]
     }
   ]
 }
}
```

<div style="page-break-after: always; margin-top: 40px;"></div>

### 6.6. Update Vc Status

Changes the status of the VC.

| Item          | Description         | Remarks |
| ------------- | ------------------- | ------- |
| Method        | `POST`              |         |
| Path          | `/api/v1/vc/status` |         |
| Authorization | -                   |         |

#### 6.6.1. Request

**■ HTTP Headers**

| Header           | Value                            | Remarks |
| ---------------- | -------------------------------- | ------- |
| + `Content-Type` | `application/json;charset=utf-8` |         |     

**■ Path Parameters**

N/A

**■ Query Parameters**

N/A

**■ HTTP Body**

```c#
def object UpdateVcStatus: "Update VC Status request message"
{    
   + vcId "vcId"    : "VC id"
   + VC_STATUS "vcStatus" : "VC Status" // Refer to data specification
}
```

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.6.2. Response

**■ Process**
1. Retrieve VC Metadata by vcId
1. Verify if VC status change is possible
1. Change VC status

**■ Status 200 - Success**

```c#
def object _UpdateVcStatus: "Update VC Status response message"
{
   + uuid  "txId": "transaction id"
}
```

**■ Status 400 - Client error**

| Code | Description        |
|------|--------------------|
| SSRVISS00206 | Non-existent `vcId`. |
| SSRVISS00209 | Already revoked `VC`. |

**■ Status 500 - Server error**

| Code | Description             |
|------|-------------------------|
| SSRVISS00016 | Failed to process 'update-vc-status' API request. |

<div style="page-break-after: always; margin-top: 30px;"></div>

#### 6.6.3. Example

**■ Request**

```shell
curl -v -X POST "http://${Host}:${Port}/issuer/api/v1/vc/status" \
-H "Content-Type: application/json;charset=utf-8" \
-d @"data.json"
```

```json
//data.json
{
   "vcId":"4d2c9387-7239-43c0-b829-c899d4aeac19",
   "vcStatus":"ACTIVE"
}
```

**■ Response**

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8

{
   "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0"
}
```
