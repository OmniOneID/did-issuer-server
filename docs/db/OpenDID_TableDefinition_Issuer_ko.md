
Open DID Issuer Database Table Definition
==

- Date: 2025-05-30
- Version: v2.0.0

Contents
--
- [Open DID Issuer Database Table Definition](#open-did-issuer-database-table-definition)
  - [Contents](#contents)
  - [1. Overview](#1-overview)
    - [1.1 ERD](#11-erd)
  - [2. Table Definition](#2-table-definition)
    - [2.1 TRANSACTION](#21-transaction)
    - [2.2 SUB\_TRANSACTION](#22-sub_transaction)
    - [2.3 CERTIFICATE\_VC](#23-certificate_vc)
    - [2.4 USER](#24-user)
    - [2.5 VC\_OFFER](#25-vc_offer)
    - [2.6 VC\_PROFILE](#26-vc_profile)
    - [2.7 VC](#27-vc)
    - [2.8 E2E](#28-e2e)
    - [2.9 REVOKE\_VC](#29-revoke_vc)
    - [2.10 VC\_SCHEMA](#210-vc_schema)
    - [2.11 VC\_SCHEMA\_NAMESPACE](#211-vc_schema_namespace)
    - [2.12 NAMESPACE](#212-namespace)
    - [2.13 ISSUE\_PROFILE](#213-issue_profile)
    - [2.14 ISSUER](#214-issuer)
    - [2.15 ZKP\_SCHEMA](#215-zkp_schema)
    - [2.16 ZKP\_ATTRIBUTE](#216-zkp_attribute)
    - [2.17 ZKP\_CREDENTIAL\_DEFINITION](#217-zkp_credential_definition)
    - [2.18 APPLICATION\_CONFIG](#218-application_config)
    - [2.19 ZKP\_NAMESPACE](#219-zkp_namespace)
    - [2.20 ZKP\_SCHEMA\_ATTRIBUTE](#220-zkp_schema_attribute)
    - [2.21 DID\_DOCUMENT](#221-did_document)
    - [2.22 ADMIN](#222-admin)

## 1. Overview

이 문서는 Issuer 서버에서 사용하는 데이터베이스 테이블의 구조를 정의합니다. 각 테이블의 필드 속성, 관계, 데이터 흐름을 설명하며, 시스템 개발 및 유지보수를 위한 필수 참고 자료로 사용됩니다.

### 1.1 ERD

Issuer 서버 데이터베이스의 테이블 간 관계를 시각적으로 보여주는 ERD는 다음 링크에서 확인할 수 있습니다: [ERD](https://www.erdcloud.com/d/d6N5gSY5C4TATnxNR)

## 2. Table Definition

### 2.1 TRANSACTION

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
| --- | ---------------- | ----------- | ---- | ------- | ------ | ----------------------- |
| PK  | id               | BIGINT      |      | NO      | N/A    | id                      |
|     | tx_id            | VARCHAR     | 40   | NO      | N/A    | transaction ID          |
|     | type             | VARCHAR     | 50   | NO      | N/A    | transaction type        |
|     | status           | VARCHAR     | 50   | NO      | N/A    | status                  |
|     | vc_plan_id       | VARCHAR     | 20   | YES     | N/A    | VC plan id              |
|     | ref_id           | VARCHAR     | 20   | YES     | N/A    | reference ID            |
|     | expired_at       | TIMESTAMP   |      | YES     | N/A    | expiration date         |
|     | created_at       | TIMESTAMP   |      | NO      | NOW()  | created date            |
|     | updated_at       | TIMESTAMP   |      | YES     | N/A    | updated date            |
| FK  | issue_profile_id | BIGINT      |      | NO      | N/A    | Linked issue profile ID |

### 2.2 SUB_TRANSACTION

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
| --- | ---------------- | --------- | ------ | -------- | ------- | ----------------- |
| PK  | id               | BIGINT    |        | NO       | N/A     | id                |
|     | step             | INTEGER   |        | NO       | N/A     | step              |
|     | type             | VARCHAR   | 50     | NO       | N/A     | transaction type  |
|     | status           | VARCHAR   | 50     | NO       | N/A     | status            |
|     | created_at       | TIMESTAMP |        | NO       | NOW()   | created date      |
|     | updated_at       | TIMESTAMP |        | YES      | N/A     | updated date      |
| FK  | transaction_id   | BIGINT    |        | NO       | N/A     | transaction key   |
| FK  | issue_profile_id | BIGINT    |        | NO       | N/A     | issue profile key |

### 2.3 CERTIFICATE_VC

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | vc           | TEXT      |        | NO       | N/A     | vc                     |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | create date            |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | update date            |

### 2.4 USER

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id           |
|     | pii         | VARCHAR   | 100    | YES      | N/A     | pii          |
|     | data        | TEXT      |        | NO       | N/A     | user data    |
|     | did         | VARCHAR   | 200    | YES      | N/A     | did          |
|     | created_at  | TIMESTAMP |        | NO       | NOW()   | created date |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date |
|     | schema_id   | VARCHAR   | 100    | NO       | N/A     | schema id    |

### 2.5 VC_OFFER

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|-----------------|-----------|--------|----------|---------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                     |
|      | offer_id        | VARCHAR   | 40     | NO       | N/A     | offer id               |
|      | offer_type      | VARCHAR   | 50     | NO       | N/A     | offer type             |
|      | vc_plan_id      | VARCHAR   | 20     | NO       | N/A     | VC plan id             |
|      | did             | VARCHAR   | 200    | NO       | N/A     | Issuer DID             |
|      | valid_until     | TIMESTAMP |        | YES      | N/A     | offer valid until      |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id  | BIGINT    |        | YES      | N/A     | transaction key        |

### 2.6 VC_PROFILE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|----------------|-----------|--------|----------|---------|------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | id                     |
|      | profile_id     | VARCHAR   | 40     | NO       | N/A     | profile id             |
|      | did            | VARCHAR   | 200    | NO       | N/A     | holder DID             |
|      | nonce          | VARCHAR   | 100    | NO       | N/A     | Issuer Nonce           |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id | BIGINT    |        | NO       | N/A     | transaction key        |
| FK   | user_id        | BIGINT    |        | YES      | N/A     | user key               |


### 2.7 VC

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
| --- | ------------- | --------- | ------ | -------- | ------- | ------------------------------- |
| PK  | id            | BIGINT    |        | NO       | N/A     | id                              |
|     | issued_at     | TIMESTAMP |        | NO       | N/A     | Issued date                     |
|     | did           | VARCHAR   | 200    | NO       | N/A     | holder DID                      |
|     | vc_id         | VARCHAR   | 40     | NO       | N/A     | VC ID                           |
|     | tx_id         | VARCHAR   | 40     | NO       | N/A     | transaction ID                  |
|     | expired_at    | TIMESTAMP |        | NO       | N/A     | expiration date                 |
|     | vc_plan_id    | VARCHAR   | 20     | NO       | N/A     | VC plan id                      |
|     | created_at    | TIMESTAMP |        | NO       | NOW()   | created date                    |
|     | updated_at    | TIMESTAMP |        | YES      | N/A     | updated date                    |
|     | vc_type       | VARCHAR   | 50     | NO       | VC      | Type of VC, e.g., 'VC' or 'ZKP' |
|     | vc_schema_id  | VARCHAR   | 100    | NO       | N/A     | Associated VC schema ID         |
|     | definition_id | VARCHAR   | 100    | YES      | N/A     | ZKP Credential definition ID    |
| FK  | user_id       | BIGINT    |        | NO       | N/A     | User key                        |

### 2.8 E2E

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|-----------------|-----------|--------|----------|---------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                     |
|      | session_key     | VARCHAR   | 300    | NO       | N/A     | session key            |
|      | nonce           | VARCHAR   | 100    | NO       | N/A     | nonce                  |
|      | curve           | VARCHAR   | 20     | NO       | N/A     | curve                  |
|      | cipher          | VARCHAR   | 20     | NO       | N/A     | cipher                 |
|      | padding         | VARCHAR   | 20     | NO       | N/A     | padding                |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id  | BIGINT    |        | NO       | N/A     | transaction key        |


### 2.9 REVOKE_VC

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|-----------------|-----------|--------|----------|---------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                     |
|      | nonce           | VARCHAR   | 100    | NO       | N/A     | Issuer Nonce           |
|      | vc_id           | VARCHAR   | 40     | NO       | N/A     | VC ID                  |
|      | status          | VARCHAR   | 20     | YES      | N/A     | revoke status          |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id  | BIGINT    |        | NO       | N/A     | transaction key        |

### 2.10 VC_SCHEMA

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | schema_id    | VARCHAR   | 100    | NO       | N/A     | schema id              |
|      | title        | VARCHAR   | 100    | NO       | N/A     | schema title           |
|      | description  | VARCHAR   | 200    | YES      | N/A     | schema description     |
|      | language     | VARCHAR   | 10     | YES      | N/A     | schema language        |
|      | version      | VARCHAR   | 10     | YES      | N/A     | schema version         |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date           |

### 2.11 VC_SCHEMA_NAMESPACE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|---------------|-----------|--------|----------|---------|------------------------|
| PK   | id            | BIGINT    |        | NO       | N/A     | id                     |
| FK   | vc_schema_id  | BIGINT    |        | NO       | N/A     | VC schema key          |
| FK   | namespace_id  | BIGINT    |        | NO       | N/A     | namespace key          |

### 2.12 NAMESPACE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|----------------|-----------|--------|----------|---------|------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | id                     |
|      | namespace_id   | VARCHAR   | 100    | NO       | N/A     | namespace id           |
|      | name           | VARCHAR   | 100    | NO       | N/A     | namespace name         |
|      | ref            | VARCHAR   | 200    | NO       | N/A     | reference URL or info  |
|      | schema_claims  | TEXT      |        | NO       | N/A     | claims defined in JSON |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |

### 2.13 ISSUE_PROFILE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
| ------ | ------------- | --------- | ------ | -------- | --------- | ------------------------------- |
| PK     | id            | BIGINT    |        | NO       | N/A       | id                              |
| PK, FK | vc_schema_id  | BIGINT    |        | NO       | N/A       | VC schema key                   |
|        | vc_plan_id    | VARCHAR   | 20     | NO       | N/A       | VC plan id                      |
|        | title         | VARCHAR   | 100    | NO       | N/A       | title                           |
|        | description   | VARCHAR   | 200    | YES      | N/A       | description                     |
|        | endpoints     | VARCHAR   | 200    | NO       | N/A       | issue API endpoint list         |
|        | cipher        | VARCHAR   | 64     | NO       | N/A       | cipher algorithm                |
|        | curve         | VARCHAR   | 64     | NO       | N/A       | curve type                      |
|        | padding       | VARCHAR   | 64     | NO       | N/A       | padding method                  |
|        | initiate_type | VARCHAR   | 20     | NO       | user init | initiate type                   |
|        | language      | VARCHAR   | 10     | NO       | N/A       | language code                   |
|        | tags          | VARCHAR   | 200    | YES      | N/A       | Tags used for profile grouping  |
|        | zkp_enabled   | BOOLEAN   |        | NO       | false     | Whether ZKP issuance is enabled |
|        | definition_id | VARCHAR   | 100    | YES      | N/A       | ZKP Credential definition ID    |
|        | created_at    | TIMESTAMP |        | NO       | NOW()     | Creation timestamp              |
|        | updated_at    | TIMESTAMP |        | YES      | N/A       | Last update timestamp           |

### 2.14 ISSUER

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|------------------|-----------|--------|----------|---------|------------------------------|
| PK   | id               | BIGINT    |        | NO       | N/A     | id                           |
|      | did              | VARCHAR   | 200    | NO       | N/A     | issuer DID                   |
|      | name             | VARCHAR   | 200    | NO       | N/A     | issuer name                  |
|      | status           | VARCHAR   | 50     | NO       | N/A     | issuer status                |
|      | server_url       | VARCHAR   | 2000   | YES      | N/A     | issuer server URL            |
|      | certificate_url  | VARCHAR   | 2000   | YES      | N/A     | issuer certificate VC URL    |
|      | created_at       | TIMESTAMP |        | NO       | NOW()   | created date                 |
|      | updated_at       | TIMESTAMP |        | YES      | N/A     | updated date                 |

### 2.15 ZKP_SCHEMA

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|-------------|-----------|--------|----------|---------|-----------------------------------------|
| PK   | id          | BIGINT    |        | NO       | N/A     | ID                                       |
|      | schema_id   | VARCHAR   | 100    | NO       | N/A     | Unique schema identifier                 |
|      | name        | VARCHAR   | 100    | NO       | N/A     | Schema name                              |
|      | version     | VARCHAR   | 10     | NO       | N/A     | Schema version                           |
|      | tag         | VARCHAR   | 100    | NO       | N/A     | Schema tag                               |
|      | status      | VARCHAR   | 50     | NO       | N/A     | Schema status                            |
|      | schema      | TEXT      |        | NO       | N/A     | JSON schema definition                   |
|      | created_at  | TIMESTAMP |        | NO       | NOW()   | Creation timestamp                       |
|      | updated_at  | TIMESTAMP |        | YES      | N/A     | Last update timestamp                    |

### 2.16 ZKP_ATTRIBUTE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|------------------|-----------|--------|----------|---------|----------------------------------------|
| PK   | id               | BIGINT    |        | NO       | N/A     | ID                                      |
|      | label            | VARCHAR   | 100    | NO       | N/A     | Attribute label                         |
|      | type             | VARCHAR   | 50     | NO       | N/A     | Attribute data type                     |
|      | caption          | VARCHAR   | 100    | NO       | N/A     | Display name for UI                     |
|      | created_at       | TIMESTAMP |        | NO       | NOW()   | Creation timestamp                      |
|      | updated_at       | TIMESTAMP |        | YES      | N/A     | Last update timestamp                   |
| FK   | zkp_namespace_id | BIGINT    |        | NO       | N/A     | Related ZKP namespace                   |


### 2.17 ZKP_CREDENTIAL_DEFINITION

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|------------------|-----------|--------|----------|---------|-----------------------------------------|
| PK   | id               | BIGINT    |        | NO       | N/A     | ID                                       |
|      | definition_id    | VARCHAR   | 100    | NO       | N/A     | Unique credential definition ID          |
|      | schema_id        | VARCHAR   | 100    | NO       | N/A     | Related schema ID                        |
|      | type             | VARCHAR   | 50     | NO       | CL      | Type of definition (e.g., CL)            |
|      | alias            | VARCHAR   | 50     | NO       | N/A     | Friendly name                            |
|      | tag              | VARCHAR   | 100    | NO       | N/A     | Tag identifier                           |
|      | version          | VARCHAR   | 10     | NO       | N/A     | Version string                           |
|      | definition       | TEXT      |        | NO       | N/A     | Full JSON definition                     |
|      | status           | VARCHAR   | 50     | NO       | N/A     | Definition status                        |
|      | created_at       | TIMESTAMP |        | NO       | NOW()   | Creation timestamp                       |
|      | updated_at       | TIMESTAMP |        | YES      | N/A     | Last update timestamp                    |
| FK   | zkp_schema_id    | BIGINT    |        | NO       | N/A     | Related ZKP schema                       |

### 2.18 APPLICATION_CONFIG

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|-----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                              |
|      | tas_url         | VARCHAR   | 2000   | YES      | N/A     | TA Server base URL              |
|      | assert_key_id   | VARCHAR   | 30     | YES      | N/A     | assertion key ID                |
|      | revoke_auth_type| VARCHAR   | 30     | YES      | N/A     | revoke authorization type       |
|      | created_at      | TIMESTAMP |        | NO       | N/A     | created date                    |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date                    |

### 2.19 ZKP_NAMESPACE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | ID                              |
|      | namespace_id   | VARCHAR   | 100    | NO       | N/A     | Unique ZKP namespace ID         |
|      | name           | VARCHAR   | 100    | NO       | N/A     | Namespace name                  |
|      | ref            | VARCHAR   | 200    | YES      | N/A     | Reference URL or information    |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | Creation timestamp              |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | Last update timestamp           |

---

### 2.20 ZKP_SCHEMA_ATTRIBUTE

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|--------------------|-----------|--------|----------|---------|--------------------------------------|
| PK   | id                 | BIGINT    |        | NO       | N/A     | ID                                   |
|      | schema_id          | VARCHAR   | 100    | NO       | N/A     | Schema ID                            |
|      | attribute_label    | VARCHAR   | 100    | NO       | N/A     | Attribute label                      |
|      | namespace_id       | VARCHAR   | 100    | NO       | N/A     | Namespace ID                         |
|      | sort_order         | SMALLINT  |        | NO       | N/A     | Attribute order                      |
| FK   | zkp_attribute_id   | BIGINT    |        | NO       | N/A     | Linked ZKP attribute ID              |
| FK   | zkp_schema_id      | BIGINT    |        | NO       | N/A     | Linked ZKP schema ID                 |

---

### 2.21 DID_DOCUMENT

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | ID                              |
|      | did_document   | TEXT      |        | NO       | N/A     | Raw DID Document JSON           |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | Creation timestamp              |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | Last update timestamp           |


### 2.22 ADMIN

| 키  | 컬럼명           | 데이터 타입 | 길이 | 널 허용 | 기본값 | 설명                    |
|------|------------------------|-----------|--------|----------|---------|-----------------------------------|
| PK   | id                     | BIGINT    |        | NO       | N/A     | id                                |
|      | login_id               | VARCHAR   | 50     | NO       | N/A     | admin login ID                    |
|      | login_password         | VARCHAR   | 64     | NO       | N/A     | hashed login password             |
|      | name                   | VARCHAR   | 200    | YES      | N/A     | admin name                        |
|      | email                  | VARCHAR   | 100    | YES      | N/A     | email address                     |
|      | email_verified         | BOOLEAN   |        | YES      | false   | whether email is verified         |
|      | require_password_reset | BOOLEAN   |        | NO       | true    | require password change on login  |
|      | role                   | VARCHAR   | 50     | YES      | N/A     | admin role                        |
|      | created_by             | VARCHAR   | 50     | NO       | N/A     | creator login ID or system user   |
|      | created_at             | TIMESTAMP |        | NO       | NOW()   | created date                      |
|      | updated_at             | TIMESTAMP |        | YES      | N/A     | updated date                      |
