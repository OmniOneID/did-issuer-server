# SD-JWT VC와 mdoc 폐기 API

- 상태: 구현 반영 (`JWT Revocation Token` 방식)
- 작성일: 2026년 10월 13일
- 최종 수정일: 2026년 10월 21일
- 적용 대상: Status List가 할당된 `dc+sd-jwt*`, `mso_mdoc*` 형식 Credential (예: `dc+sd-jwt-did`, `mso_mdoc-did`)
- 관련 문서: [Issuer API - 7.2. Credential 폐기 Endpoint](Issuer_API_ko.md#72-credential-폐기-endpoint)

## 1. 개요

본 문서는 Wallet이 Issuer에 직접 SD-JWT VC 또는 mdoc의 폐기를 요청하는 API를 정의한다. Wallet은 Issuer Metadata의 기존 `nonce_endpoint`에서 `c_nonce`를 받은 후, 폐기 대상의 Status List 정보와 nonce를 사용자 DID 키로 서명하여 Issuer에 전달한다.

```text
Wallet → Issuer nonce_endpoint → c_nonce
Wallet → Issuer revocation endpoint → JWT Revocation Token
Issuer → Status List 상태를 INVALID로 변경
```

이 API는 Access Token과 TAS를 사용하지 않는다. `nonce_endpoint`는 OID4VCI의 기존 endpoint를 사용하고, 폐기 endpoint와 JWT Revocation Token 형식은 OpenDID 생태계 확장으로 정의한다.

## 2. 적용 범위

포함하는 기능은 다음과 같다.

- Wallet이 보유한 Credential 한 건의 폐기 요청
- 기존 OID4VCI `nonce_endpoint` 재사용
- DID authentication key를 이용한 JWT 서명
- `status_list_uri + index`를 이용한 발급본 식별
- Credential 상태를 `INVALID`로 변경
- Status List version 및 상태 변경 이력 갱신

다음 기능은 이 API의 범위에 포함하지 않는다.

- Credential 발급
- Credential 로컬 삭제
- `SUSPENDED` 상태 전환과 복구
- DID 키를 사용할 수 없는 사용자의 복구 절차
- Issuer 관리자 또는 내부 업무 시스템에 의한 상태 변경 (관리자 상태 변경은 Admin API `PATCH /issuer/admin/v1/oid4vci/issued-credentials/{id}/status`로 별도 제공)

## 3. 표준과 확장 범위

| 항목 | 구분 |
|---|---|
| `credential_issuer`와 Issuer Metadata 조회 | OID4VCI 표준 |
| `nonce_endpoint`와 `c_nonce` 응답 | OID4VCI 표준 |
| SD-JWT VC와 mdoc의 Status List 정보 | Credential 포맷 및 Status List 규격 |
| `credential_revocation_endpoint` | OpenDID 확장 |
| JWT Revocation Token을 이용한 폐기 요청 | OpenDID 확장 |
| Credential 상태를 `INVALID`로 변경 | Issuer 내부 lifecycle 처리 |

기존 OID4VCI `nonce_endpoint`는 Credential 발급 proof를 위한 endpoint다. 동일 endpoint에서 받은 nonce를 폐기에 사용하는 것은 OpenDID 확장이다. 요청의 용도는 전용 폐기 endpoint와 요청 DTO로 구분하며, Issuer는 nonce를 발급과 폐기를 통틀어 한 번만 소비한다.

## 4. Issuer Metadata

Wallet은 발급 당시 저장한 `credential_issuer`를 이용해 Issuer Metadata를 조회한다.

```http
GET /.well-known/openid-credential-issuer
```

Issuer Metadata에는 기존 `nonce_endpoint`와 OpenDID 확장 폐기 endpoint를 함께 제공한다.

```json
{
  "credential_issuer": "https://issuer.example.com",
  "credential_endpoint": "https://issuer.example.com/credential",
  "nonce_endpoint": "https://issuer.example.com/nonce",
  "credential_revocation_endpoint": "https://issuer.example.com/credential-lifecycle/revoke"
}
```

### 4.1. Metadata 필드

| 필드 | 필수 | 설명 |
|---|---:|---|
| `credential_issuer` | Y | Credential Issuer 식별자 |
| `nonce_endpoint` | Y | 기존 OID4VCI nonce 발급 URL |
| `credential_revocation_endpoint` | Y | OpenDID 확장 폐기 요청 전체 URL |

Issuer Server는 이 값을 `{credential_issuer}/credential-lifecycle/revoke`로 고정 생성한다(`credential_issuer` 끝의 `/`는 제거). Admin Console과 Metadata 수정 API로 다른 경로를 설정할 수 없으며, 저장된 값이 달라도 Metadata 조회 시 고정 값으로 대체된다. 서버 내부 경로도 시작 시 `/credential-lifecycle/revoke`로 고정 등록되며, 인증 없이 호출할 수 있도록 Security 설정에서 허용한다.

Wallet은 `credential_issuer`에 고정 경로를 붙이거나 Status List URI에서 폐기 URL을 추론하면 안 된다. Metadata에 들어 있는 전체 URL을 사용해야 한다.

## 5. 전체 처리 흐름

```text
1. Wallet이 Credential과 함께 저장한 credential_issuer를 확인한다.
2. Wallet이 Issuer Metadata를 조회한다.
3. Wallet이 nonce_endpoint에 POST 요청을 보낸다.
4. Issuer가 c_nonce를 생성하고 저장한 뒤 Wallet에 반환한다.
5. Wallet이 폐기 대상과 nonce를 포함한 JWT Payload를 생성한다.
6. Wallet이 DID authentication key로 JWT를 서명한다.
7. Wallet이 credential_revocation_endpoint로 요청을 보낸다.
8. Issuer가 JWT 서명과 발급본 소유 관계를 검증한다.
9. Issuer가 nonce의 유효성을 확인하고 일회성으로 소비한다.
10. Issuer가 Credential 상태를 INVALID로 변경한다.
11. Issuer가 Status List version과 상태 변경 이력을 갱신한다.
12. Wallet이 로컬 Credential을 폐기 상태로 표시한다.
```

## 6. Nonce 요청

기존 OID4VCI `nonce_endpoint`를 그대로 사용한다.

| 항목 | 값 |
|---|---|
| Method | `POST` |
| URL | Issuer Metadata의 `{nonce_endpoint}` |
| Authorization | 없음 |
| Request Body | 없음 |

### 6.1. 요청 예시

```http
POST /nonce HTTP/1.1
Host: issuer.example.com
Content-Length: 0
```

### 6.2. 성공 응답

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8
```

```json
{
  "c_nonce": "8qvJQO7TjZlE5M0wL8OaGA"
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `c_nonce` | String | Y | 16바이트 난수를 Base64URL without padding으로 인코딩한 값 |

현재 nonce 유효시간은 서버 저장 기준 10분이다. Wallet은 nonce를 받은 후 즉시 폐기 요청을 생성해야 한다. 동일 nonce는 Credential 발급과 폐기 중 한 요청에서만 사용할 수 있다.

nonce는 `spring.datasource.url`이 설정된 경우 `t_oid4vc_cnonce` 기반 DB 저장소(`JpaCNonceStore`)에 저장된다. DB 저장소가 없으면 SDK의 In-memory 저장소가 사용되며, 이 경우 만료 시간이 적용되지 않는다.

## 7. Credential 폐기 요청

| 항목 | 값 |
|---|---|
| Method | `POST` |
| URL | Issuer Metadata의 `{credential_revocation_endpoint}` |
| Authorization | 없음 |
| Content-Type | `application/json;charset=utf-8` |
| Idempotency | Credential의 최종 `INVALID` 상태 기준 |

### 7.1. 요청 Body

폐기 요청은 JWS Compact Serialization 형식의 JWT Revocation Token을 사용한다. HTTP Request Body에는 서명된 JWT만 전달한다.

```json
{
  "revocation_token": "eyJhbGciOiJFUzI1NiIsInR5cCI6Im9wZW5kaWQtY3JlZGVudGlhbC1yZXZvY2F0aW9uK2p3dCIsImtpZCI6ImRpZDpvbW46aG9sZGVyP3ZlcnNpb25JZD0xI2F1dGgifQ.payload.signature"
}
```

JWT Protected Header를 디코딩한 예시는 다음과 같다.

```json
{
  "alg": "ES256",
  "typ": "opendid-credential-revocation+jwt",
  "kid": "did:omn:holder?versionId=1#auth"
}
```

JWT Payload를 디코딩한 예시는 다음과 같다.

```json
{
  "iss": "did:omn:holder",
  "iat": 1791937200,
  "exp": 1791937500,
  "nonce": "8qvJQO7TjZlE5M0wL8OaGA",
  "credential_status": {
    "status_list": {
      "uri": "https://issuer.example.com/status-lists/1",
      "idx": 42
    }
  }
}
```

`nonce`에는 `nonce_endpoint`가 반환한 `c_nonce` 값을 그대로 넣는다. `did`는 `iss`와 중복되므로 사용하지 않는다.

### 7.2. Request Body 필드

| 필드 | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `revocation_token` | String | Y | JWS Compact Serialization 형식의 서명된 JWT |

### 7.3. JWT Protected Header

| 필드 | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `alg` | String | Y | 반드시 `ES256`; `none` 및 다른 알고리즘은 허용하지 않음 |
| `typ` | String | Y | 반드시 `opendid-credential-revocation+jwt` |
| `kid` | String | Y | Holder DID의 `authentication` verification method를 나타내는 DID URL. `?` 또는 `#`를 포함해야 하며, 그 앞의 DID가 `iss`와 같아야 함 |

Header 전체는 JWS Protected Header로 서명 범위에 포함한다.

### 7.4. JWT Payload Claims

| Claim | 타입 | 필수 | 설명 |
|---|---|---:|---|
| `iss` | String | Y | JWT를 서명한 Holder DID. 발급 레코드의 `subject_did`와 일치해야 함 |
| `iat` | Number | Y | JWT 생성 시각을 나타내는 NumericDate |
| `exp` | Number | Y | JWT 만료시각을 나타내는 NumericDate. `iat` 이후 최대 5분 |
| `nonce` | String | Y | `nonce_endpoint`가 반환한 `c_nonce` 값 |
| `credential_status.status_list.uri` | String | Y | Credential에 포함된 Status List URI |
| `credential_status.status_list.idx` | Number | Y | Credential에 포함된 Status List index. 0 이상의 정수 |

`iat`, `nonce`는 [OID4VCI 1.0 Final](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0-final.html)의 JWT proof claim 구성을 참고한다. `iss`, `exp`, `credential_status`와 `opendid-credential-revocation+jwt` 타입은 이 폐기 API가 정의하는 OpenDID 확장이다. 이 방식은 JWT와 JWS 표준을 사용하지만 표준 OID4VCI 폐기 프로토콜을 의미하지 않는다.

`aud`는 Issuer Metadata에서 획득한 Issuer 전용 `credential_revocation_endpoint`로 직접 요청하고, 서버가 Status List URI와 index의 소유 관계를 별도로 확인하므로 사용하지 않는다. `sub`는 `iss`와 동일한 Holder DID를 반복하므로 사용하지 않는다. `nbf`는 짧은 수명의 `iat`과 `exp`로 대체한다. `jti`는 별도 멱등성 저장소를 만들지 않고 nonce의 원자적 일회성 소비와 Credential의 최종 `INVALID` 상태로 재전송을 처리하므로 사용하지 않는다.

### 7.5. 상태 변경 사유

Wallet은 폐기 사유를 전달하지 않는다. Issuer는 인증과 소유 관계 검증에 성공한 Wallet 요청을 처리할 때 상태 변경 이력의 사유를 `USER_REQUEST`로 기록한다.

## 8. 요청 서명 생성 규칙

### 8.1. 서명 키

- `kid`는 발급 당시 저장된 `subject_did`가 제어하는 키여야 한다.
- 해당 키는 DID Document의 `authentication` verification relationship에 포함되어야 한다.
- 초기 지원 곡선은 `secp256r1`로 제한하며 JWS 알고리즘은 `ES256`을 사용한다.
- 현재 구현은 `kid`의 DID와 `iss`, 발급 레코드의 `subject_did`가 같은지 확인한다. verification method의 `controller` 검증은 테스트 목적으로 임시 비활성화되어 있다(9.1 참고).

### 8.2. JWT 생성

1. 7.3의 Protected Header와 7.4의 Payload를 생성한다.
2. Header와 Payload의 UTF-8 JSON 바이트를 각각 Base64URL without padding으로 인코딩한다.
3. `BASE64URL(header) + "." + BASE64URL(payload)`를 JWS Signing Input으로 사용한다.
4. `kid`가 가리키는 Holder DID authentication private key로 ES256 서명을 생성한다.
5. ES256 서명은 JWS 규칙에 따라 32바이트 `R`과 32바이트 `S`를 연결한 64바이트 값을 Base64URL without padding으로 인코딩한다.
6. `BASE64URL(header) + "." + BASE64URL(payload) + "." + BASE64URL(signature)` 형식의 JWT를 만든다.
7. 생성된 JWT를 Request Body의 `revocation_token`에 넣는다.

JWT는 JWS Signing Input 자체를 검증하므로 Issuer가 Payload JSON을 다시 정렬하거나 직렬화하면 안 된다. 디코딩한 claim은 서명 검증에 성공하기 전까지 신뢰하지 않는다.

JWT claim과 JWS 생성 및 검증은 [RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)와 [RFC 7515](https://datatracker.ietf.org/doc/html/rfc7515)를 따른다.

## 9. Issuer 검증 규칙

### 9.1. JWT 검증

JWT 검증은 OID4VCI SDK의 `CredentialRevocationTokenVerifier`가 수행하고, Holder 공개키 조회는 Issuer Server의 `CredentialRevocationService`가 수행한다.

1. `revocation_token`이 비어 있지 않고 세 개의 JWS Compact Serialization 구간으로 구성되었는지 확인한다.
2. Protected Header의 `alg`가 `ES256`, `typ`이 `opendid-credential-revocation+jwt`인지 확인한다.
3. `kid`와 `iss`가 있는지 확인하고, `kid`의 `?` 또는 `#` 앞 DID가 `iss`와 같은지 확인한다.
4. `iat`과 `exp`가 NumericDate이고, `exp > iat`, `exp - iat <= 300`인지 확인한다.
5. 최대 60초의 clock skew를 고려해 JWT가 미래에 생성되지 않았고 `exp`가 지나지 않았는지 확인한다.
6. `nonce`, `credential_status.status_list.uri`, `credential_status.status_list.idx`(0 이상의 정수)가 있는지 확인한다.
7. `iss`로 DID Document를 조회하고, DID Document의 `id`(query와 fragment 제외)가 `iss`와 같은지 확인한다.
8. `kid`가 DID Document의 `authentication` 관계에 포함되고, 같은 ID의 verification method에 `publicKeyMultibase`가 있는지 확인한다. 키 참조는 `#` 뒤 fragment 기준으로 비교한다.
9. 공개키가 secp256r1(P-256) EC 키인지 확인하고 JWS Signing Input의 ES256 서명을 검증한다.
10. 검증된 Payload에서 `nonce`, Status List URI와 index 및 `iss`를 추출한다.

verification method의 `controller`가 `iss`와 일치하는지 확인하는 단계는 TAS가 제어하는 verification method를 테스트에서 허용하기 위해 현재 코드에서 임시 비활성화되어 있다.

### 9.2. 소유 관계 검증 및 상태 변경

1. `status_list.uri + idx`로 발급 레코드를 행 잠금(`PESSIMISTIC_WRITE`)으로 조회한다.
2. 발급 레코드가 `ISSUED` 상태인지 확인한다.
3. 검증된 Holder DID가 발급 레코드의 `subject_did`와 일치하는지 확인한다. `subject_did`가 비어 있고 `user_id`가 `did:`로 시작하면 `user_id`를 대신 비교한다.
4. nonce를 원자적으로 소비한다. nonce가 존재하고 만료되지 않았으며 아직 사용되지 않은 경우에만 `consumed_at`을 기록한다.
5. 현재 상태가 이미 `INVALID`이면 기존 `status_changed_at`을 `revoked_at`으로 하여 성공 결과를 반환한다.
6. 그 외에는 상태 전이 정책에 따라 현재 상태가 `VALID` 또는 `SUSPENDED`인지 확인한다.
7. Credential 상태를 `INVALID`로 변경하고 `status_changed_at`을 기록한다.
8. Status List version을 증가시킨다.
9. 상태 변경 이력에 이전 상태, 변경 상태, 사유 `USER_REQUEST`, 요청 DID(`changed_by`)와 변경 시각을 기록한다.

nonce 소비와 상태 변경은 하나의 트랜잭션에서 처리한다. 동시에 같은 nonce가 들어오면 하나의 요청만 성공해야 한다. nonce는 소유 관계 검증을 통과한 뒤에 소비되므로 JWT 검증, 발급 레코드 조회와 소유 관계 검증 단계에서 실패한 요청은 nonce를 소비하지 않는다. 상태 변경이 실패하면 nonce 소비도 함께 롤백된다.

## 10. 성공 응답

```http
HTTP/1.1 200 OK
Content-Type: application/json;charset=utf-8
```

```json
{
  "credential_status": {
    "status_list": {
      "uri": "https://issuer.example.com/status-lists/1",
      "idx": 42
    },
    "status": "INVALID"
  },
  "revoked_at": "2026-10-13T03:00:01Z"
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `credential_status.status_list.uri` | String | 처리한 Status List URI |
| `credential_status.status_list.idx` | Long | 처리한 Status List index |
| `credential_status.status` | String | 변경된 상태인 `INVALID` |
| `revoked_at` | String | UTC ISO 8601 상태 변경 시각. 소수점 이하 초가 포함될 수 있음 (예: `2026-10-13T03:00:01.123456Z`) |

응답을 받지 못한 Wallet은 새로운 nonce를 받아 폐기를 다시 요청한다. 인증과 소유 관계 검증 결과 해당 Credential이 이미 `INVALID`이면 기존 성공 결과를 반환하며, 상태 변경 이력은 중복 생성하지 않는다. 이 경우에도 요청의 nonce는 소비된다.

## 11. 오류 응답

오류는 별도 API 응답 DTO를 만들지 않고 기존 `GlobalControllerAdvice`와 `ErrorResponse`를 사용한다. 서비스는 오류 상황에 맞는 `ErrorCode`를 포함한 `OpenDidException`을 발생시키며, 공통 Exception Handler가 `ErrorCode`의 HTTP 상태와 다음 형식의 응답을 생성한다.

```json
{
  "code": "SSRVISS00210",
  "description": "The issuer nonce is not valid."
}
```

`code`는 `ErrorCode`가 생성하는 `SSRVISS` 접두어를 포함한 문자열이며, `description`은 해당 `ErrorCode`의 메시지다.

### 11.1. 폐기 처리 오류 코드

기존 폐기 API와 공통 `ErrorCode`를 우선 재사용한다.

| HTTP 상태 | `ErrorCode` | 응답 `code` | 적용 조건 |
|---:|---|---|---|
| 400 | `VC_NOT_FOUND` | `SSRVISS00206` | Status List URI와 index에 해당하는 발급본이 없거나 발급 레코드가 `ISSUED` 상태가 아님 |
| 400 | `ISSUER_NONCE_INVALID` | `SSRVISS00210` | JWT의 `nonce`가 nonce 저장소에 없거나 만료되었거나 이미 사용됨 |
| 400 | `HOLDER_INVALID` | `SSRVISS00302` | 검증된 Holder DID와 발급 레코드의 `subject_did`가 다름. 또는 SDK의 JWT 검증 실패(빈 `revocation_token`, JWS 형식, `alg`, `typ`, `kid`·`iss` 불일치, 필수 Claim 누락, 유효 시간 위반, 만료, P-256 이외의 키)와 DID Document·`authentication`·verification method 확인 실패 |
| 400 | `SIGNATURE_VERIFICATION_FAILED` | `SSRVISS00413` | JWT 서명 검증 실패 또는 `publicKeyMultibase` 공개키 디코딩 실패 |
| 500 | `DID_DOCUMENT_RETRIEVAL_FAILED` | `SSRVISS00103` | DID Document 조회 중 예외 발생 |
| 500 | `TR_VC_REVOKE_FAILED` | `SSRVISS00011` | 상태 전이 불가, Status List 조회·검증 실패 등 상태 변경 처리 실패 |

SDK의 JWT 검증 오류 중 서명 검증 실패(`ERR_CODE_JWS_VERIFY_FAILED`)만 `SIGNATURE_VERIFICATION_FAILED`로 변환하고, 그 외 SDK 오류는 모두 `HOLDER_INVALID`로 변환한다. JWT에 `nonce` Claim 자체가 없으면 `ISSUER_NONCE_INVALID`가 아니라 `HOLDER_INVALID`를 응답한다.

이미 `INVALID`인 Credential은 오류가 아니라 성공으로 처리하므로 `REVOKED_VC` (`SSRVISS00209`)는 이 API에서 사용하지 않는다.

### 11.2. 공통 Exception Handler 동작

현재 `GlobalControllerAdvice`의 처리 결과는 다음과 같다.

| 발생 예외 | HTTP 상태 | 응답 `code` | 응답 `description` |
|---|---:|---|---|
| `OpenDidException(ErrorCode)` | `ErrorCode.httpStatus` | 해당 `ErrorCode.code` | 해당 `ErrorCode.message` |
| `OpenDidException(ErrorResponse)` | 400 | 전달받은 `code` | 전달받은 `description` |
| `MethodArgumentNotValidException` | 500 | `9999` | 필드 검증 메시지를 `; `로 결합한 값 |
| `HttpMessageNotReadableException` | 500 | `SSRVISS00702` | `Unable to process the request.` |
| 기타 `Exception` | 500 | `500` | 예외 메시지 |

Request DTO 검증 오류와 읽을 수 없는 JSON이 현재 500으로 반환되는 것은 기존 Handler의 동작을 그대로 기술한 것이다. 폐기 API도 별도 처리기를 추가하지 않는 한 동일하게 응답한다.

## 12. 데이터 저장 요구사항

### 12.1. 발급 레코드

`t_oid4vc_credential_issuance`에 발급 당시 검증한 DID를 저장한다.

```text
subject_did
```

`subject_did`는 폐기 JWT의 `iss`와 비교하기 위한 값이다. 변경 가능한 사용자 정보만 간접 조회하지 않고 발급 당시 값을 발급본에 고정한다.

| 항목 | 구현 |
|---|---|
| 컬럼 | `subject_did VARCHAR(512)`, 인덱스 `idx_oid4vc_issuance_subject_did` (Liquibase `set.8/add-oid4vc-revocation-columns.xml`) |
| 저장 시점 | Credential 발급 시 Status List index를 할당하면서, Proof `kid`로 확인한 Holder DID를 저장 |
| 기존 발급분 | `subject_did`가 비어 있고 `user_id`가 `did:`로 시작하면 폐기 검증 시 `user_id`를 대신 사용 |

Status List가 적용되는 `dc+sd-jwt*`, `mso_mdoc*` 형식은 발급 시 Holder DID URL `kid` Proof가 필수이므로 `subject_did`가 함께 저장된다.

### 12.2. Nonce 저장소

기존 `t_oid4vc_cnonce`를 발급과 폐기에서 공통으로 사용하되 다음 기능이 필요하다.

```text
nonce hash 저장
expires_at 검사
consumed_at 또는 consumed 상태
원자적 일회성 소비
만료 레코드 정리
```

현재 구현 상태는 다음과 같다.

| 요구사항 | 구현 상태 |
|---|---|
| nonce hash 저장 | 미구현. `cnonce` 컬럼에 원문을 저장한다. |
| `expires_at` 검사 | 구현. 저장 시 10분 후로 설정하고 조회와 소비 시 검사한다. |
| `consumed_at` | 구현. `consumed_at TIMESTAMP` 컬럼을 추가했다 (Liquibase `set.8`). |
| 원자적 일회성 소비 | 구현. 행 잠금(`PESSIMISTIC_WRITE`)으로 조회한 뒤 미사용·미만료 nonce에만 `consumed_at`을 기록한다. 발급 Proof 검증의 nonce 제거도 삭제 대신 소비로 처리한다. |
| 만료 레코드 정리 | 미구현. 만료되거나 소비된 레코드는 삭제되지 않는다. |

## 13. 보안 요구사항

- 모든 Metadata, nonce와 폐기 endpoint는 HTTPS를 사용해야 한다.
- nonce와 JWT 전문을 운영 로그에 기록하면 안 된다.
- `revocation_token`, DID, Status List URI와 index는 로그에서 마스킹한다.
- nonce endpoint와 폐기 endpoint에 IP 및 DID 기준 rate limit을 적용한다.
- DID Document는 신뢰할 수 있는 Repository 또는 Resolver에서 조회한다.
- DID Document의 key ID 존재 여부뿐 아니라 `authentication` 관계와 controller도 검증한다.
- nonce는 성공 여부와 무관하게 서명 검증 이후 정책에 따라 재사용할 수 없도록 처리한다.
- 데이터베이스의 상태 변경은 row lock을 사용해 중복 전이를 방지한다.

### 13.1. 현재 구현 상태

| 요구사항 | 구현 상태 |
|---|---|
| HTTPS 사용 | 배포 설정 사항이며 서버 코드에서 강제하지 않는다. |
| nonce와 JWT 전문 로그 금지, 마스킹 | Controller 요청·응답 전문 로그는 debug 레벨에서만 기록된다. debug 레벨을 활성화하면 `revocation_token` 전문이 기록되며, 마스킹은 구현되어 있지 않다. |
| IP 및 DID 기준 rate limit | 미구현. Gateway 등 서버 앞단에서 적용해야 한다. |
| 신뢰할 수 있는 DID Document 조회 | 구현. Issuer의 DID Document 조회 서비스(`StorageService.findDidDoc`)를 사용한다. |
| `authentication` 관계와 controller 검증 | `authentication` 관계는 검증한다. controller 검증은 테스트 목적으로 임시 비활성화되어 있다. |
| 서명 검증 이후 nonce 재사용 방지 | nonce는 소유 관계 검증을 통과한 뒤 소비된다. 그 전 단계에서 실패하거나 상태 변경이 실패해 롤백되면 nonce는 만료 전까지 다시 사용할 수 있다. |
| row lock | 구현. 발급 레코드, Status List와 nonce 레코드를 `PESSIMISTIC_WRITE`로 잠근다. |
