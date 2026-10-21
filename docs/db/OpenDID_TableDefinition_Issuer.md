
Open DID Issuer Database Table Definition
==

- Date: 2026-10-21
- Version: v2.0.1

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
  - [3. OID4VCI and Server Configuration Tables](#3-oid4vci-and-server-configuration-tables)
    - [3.1 SERVER\_CONFIG](#31-server_config)
    - [3.2 ADMIN\_PASSWORD\_POLICY](#32-admin_password_policy)
    - [3.3 CREDENTIAL\_CONFIG](#33-credential_config)
    - [3.4 T\_OID4VC\_OFFER](#34-t_oid4vc_offer)
    - [3.5 T\_OID4VC\_CNONCE](#35-t_oid4vc_cnonce)
    - [3.6 T\_OID4VC\_PRE\_AUTH\_CODE](#36-t_oid4vc_pre_auth_code)
    - [3.7 T\_OID4VC\_SESSION\_MAP](#37-t_oid4vc_session_map)
    - [3.8 T\_OID4VC\_DEFERRED\_TRANSACTION](#38-t_oid4vc_deferred_transaction)
    - [3.9 T\_OID4VC\_CLIENT](#39-t_oid4vc_client)
    - [3.10 T\_OID4VC\_USER\_CREDENTIAL\_MAP](#310-t_oid4vc_user_credential_map)
    - [3.11 T\_OID4VC\_NOTIFICATION](#311-t_oid4vc_notification)
    - [3.12 T\_OID4VC\_USER\_CLAIMS](#312-t_oid4vc_user_claims)
    - [3.13 T\_OID4VC\_WEBVIEW\_ISSUANCE\_SESSION](#313-t_oid4vc_webview_issuance_session)
    - [3.14 T\_OID4VC\_STATUS\_LIST](#314-t_oid4vc_status_list)
    - [3.15 T\_OID4VC\_CREDENTIAL\_ISSUANCE](#315-t_oid4vc_credential_issuance)
    - [3.16 T\_OID4VC\_CREDENTIAL\_STATUS\_HISTORY](#316-t_oid4vc_credential_status_history)
    - [3.17 T\_OID4VC\_STATUS\_LIST\_BOOTSTRAP\_LOCK](#317-t_oid4vc_status_list_bootstrap_lock)

## 1. Overview

This document defines the structure of the database tables used in the Issuer server. It describes the field attributes, relationships, and data flow for each table, serving as essential reference material for system development and maintenance.

### 1.1 ERD

Access the [ERD](https://www.erdcloud.com/d/d6N5gSY5C4TATnxNR) site to view the diagram, which visually represents the relationships between the tables in the Issuer server database, including key attributes, primary keys, and foreign key relationships.

## 2. Table Definition

### 2.1 TRANSACTION

| Key | Column Name      | Data Type | Length | Nullable | Default | Description             |
| --- | ---------------- | --------- | ------ | -------- | ------- | ----------------------- |
| PK  | id               | BIGINT    |        | NO       | N/A     | id                      |
|     | tx_id            | VARCHAR   | 40     | NO       | N/A     | transaction ID          |
|     | type             | VARCHAR   | 50     | NO       | N/A     | transaction type        |
|     | status           | VARCHAR   | 50     | NO       | N/A     | status                  |
|     | vc_plan_id       | VARCHAR   | 20     | YES      | N/A     | VC plan id              |
|     | ref_id           | VARCHAR   | 20     | YES      | N/A     | reference ID            |
|     | expired_at       | TIMESTAMP |        | YES      | N/A     | expiration date         |
|     | created_at       | TIMESTAMP |        | NO       | NOW()   | created date            |
|     | updated_at       | TIMESTAMP |        | YES      | N/A     | updated date            |
| FK  | issue_profile_id | BIGINT    |        | YES      | N/A     | Linked issue profile ID |

### 2.2 SUB_TRANSACTION

| Key | Column Name      | Data Type | Length | Nullable | Default | Description       |
| --- | ---------------- | --------- | ------ | -------- | ------- | ----------------- |
| PK  | id               | BIGINT    |        | NO       | N/A     | id                |
|     | step             | INTEGER   |        | NO       | N/A     | step              |
|     | type             | VARCHAR   | 50     | NO       | N/A     | transaction type  |
|     | status           | VARCHAR   | 50     | NO       | N/A     | status            |
|     | created_at       | TIMESTAMP |        | NO       | NOW()   | created date      |
|     | updated_at       | TIMESTAMP |        | YES      | N/A     | updated date      |
| FK  | transaction_id   | BIGINT    |        | NO       | N/A     | transaction key   |

### 2.3 CERTIFICATE_VC

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | vc           | TEXT      |        | NO       | N/A     | vc                     |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | create date            |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | update date            |

### 2.4 USER

| Key | Column Name  | Data Type | Length | Nullable | Default | Description   |
| --- | ------------ | --------- | ------ | -------- | ------- | ------------- |
| PK  | id           | BIGINT    |        | NO       | N/A     | id            |
|     | pii          | VARCHAR   | 100    | YES      | N/A     | pii           |
|     | data         | TEXT      |        | NO       | N/A     | user data     |
|     | did          | VARCHAR   | 200    | YES      | N/A     | did           |
|     | created_at   | TIMESTAMP |        | NO       | NOW()   | created date  |
|     | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date  |
| FK  | vc_schema_id | BIGINT    |        | NO       | 1       | VC schema key |

- Unique constraint: `user_did_vc_schema_id_key` (`did`, `vc_schema_id`)

### 2.5 VC_OFFER

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description            |
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

| Key  | Column Name    | Data Type | Length | Nullable | Default | Description            |
|------|----------------|-----------|--------|----------|---------|------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | id                     |
|      | profile_id     | VARCHAR   | 40     | NO       | N/A     | profile id             |
|      | did            | VARCHAR   | 200    | YES      | N/A     | holder DID             |
|      | nonce          | VARCHAR   | 100    | NO       | N/A     | Issuer Nonce           |
|      | zkp_nonce      | VARCHAR   | 100    | YES      | N/A     | ZKP Issuer Nonce       |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id | BIGINT    |        | NO       | N/A     | transaction key        |
| FK   | user_id        | BIGINT    |        | YES      | N/A     | user key               |


### 2.7 VC

| Key | Column Name   | Data Type | Length | Nullable | Default | Description                     |
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
|     | vc_schema_id  | VARCHAR   | 100    | NO       | ''      | Associated VC schema ID         |
|     | status        | VARCHAR   | 20     | NO       | ACTIVE  | VC status                       |
| FK  | user_id       | BIGINT    |        | NO       | N/A     | User key                        |

### 2.8 E2E

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description            |
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

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description            |
|------|-----------------|-----------|--------|----------|---------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                     |
|      | nonce           | VARCHAR   | 100    | NO       | N/A     | Issuer Nonce           |
|      | vc_id           | VARCHAR   | 40     | NO       | N/A     | VC ID                  |
|      | status          | VARCHAR   | 20     | YES      | N/A     | revoke status          |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id  | BIGINT    |        | NO       | N/A     | transaction key        |

### 2.10 VC_SCHEMA

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | vc_schema_id | VARCHAR   | 100    | NO       | N/A     | schema id              |
|      | title        | VARCHAR   | 100    | NO       | N/A     | schema title           |
|      | description  | VARCHAR   | 200    | YES      | N/A     | schema description     |
|      | language     | VARCHAR   | 10     | YES      | N/A     | schema language        |
|      | version      | VARCHAR   | 10     | YES      | N/A     | schema version         |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date           |

### 2.11 VC_SCHEMA_NAMESPACE

| Key  | Column Name   | Data Type | Length | Nullable | Default | Description            |
|------|---------------|-----------|--------|----------|---------|------------------------|
| PK   | id            | BIGINT    |        | NO       | N/A     | id                     |
| FK   | vc_schema_id  | BIGINT    |        | NO       | N/A     | VC schema key          |
| FK   | namespace_id  | BIGINT    |        | NO       | N/A     | namespace key          |

### 2.12 NAMESPACE

| Key  | Column Name    | Data Type | Length | Nullable | Default | Description            |
|------|----------------|-----------|--------|----------|---------|------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | id                     |
|      | namespace_id   | VARCHAR   | 100    | NO       | N/A     | namespace id           |
|      | name           | VARCHAR   | 100    | NO       | N/A     | namespace name         |
|      | ref            | VARCHAR   | 200    | NO       | N/A     | reference URL or info  |
|      | schema_claims  | TEXT      |        | NO       | N/A     | claims defined in JSON |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |

### 2.13 ISSUE_PROFILE

| Key    | Column Name        | Data Type | Length | Nullable | Default   | Description                                            |
| ------ | ------------------ | --------- | ------ | -------- | --------- | ------------------------------------------------------ |
| PK     | id                 | BIGINT    |        | NO       | N/A       | id                                                     |
| FK     | vc_schema_id       | BIGINT    |        | NO       | N/A       | VC schema key                                          |
|        | vc_plan_id         | VARCHAR   | 20     | NO       | N/A       | VC plan id                                             |
|        | title              | VARCHAR   | 100    | NO       | N/A       | title                                                  |
|        | description        | VARCHAR   | 200    | NO       | N/A       | description                                            |
|        | endpoints          | VARCHAR   | 2000   | NO       | N/A       | issue API endpoint list                                |
|        | cipher             | VARCHAR   | 64     | NO       | N/A       | cipher algorithm                                       |
|        | curve              | VARCHAR   | 64     | NO       | N/A       | curve type                                             |
|        | padding            | VARCHAR   | 64     | NO       | N/A       | padding method                                         |
|        | initiate_type      | VARCHAR   | 20     | YES      | N/A       | initiate type (`USER_INIT`, `ISSUER_INIT`)             |
|        | language           | VARCHAR   | 10     | NO       | N/A       | language code                                          |
|        | tags               | VARCHAR   | 200    | NO       | N/A       | Tags used for profile grouping                         |
|        | zkp_enabled        | BOOLEAN   |        | NO       | false     | Whether ZKP issuance is enabled                        |
|        | definition_id      | VARCHAR   | 100    | YES      | N/A       | ZKP Credential definition ID                           |
|        | issuance_mode      | VARCHAR   | 20     | NO       | DIRECT    | VC issuance mode (`DIRECT`, `PROXY`)                   |
|        | user_query_type    | VARCHAR   | 10     | NO       | DB        | User data query method (`DB`, `API`, `TEST`)           |
|        | user_query_url     | VARCHAR   | 2000   | YES      | N/A       | User data query API URL (`API` method)                 |
|        | user_query_headers | TEXT      |        | YES      | N/A       | User data query API request headers (`API` method)     |
|        | created_at         | TIMESTAMP |        | NO       | NOW()     | Creation timestamp                                     |
|        | updated_at         | TIMESTAMP |        | YES      | N/A       | Last update timestamp                                  |

### 2.14 ISSUER

| Key  | Column Name               | Data Type | Length | Nullable | Default | Description                              |
|------|---------------------------|-----------|--------|----------|---------|------------------------------------------|
| PK   | id                        | BIGINT    |        | NO       | N/A     | id                                       |
|      | did                       | VARCHAR   | 200    | YES      | N/A     | issuer DID                               |
|      | name                      | VARCHAR   | 200    | NO       | N/A     | issuer name                              |
|      | status                    | VARCHAR   | 50     | NO       | N/A     | issuer status                            |
|      | server_url                | VARCHAR   | 2000   | YES      | N/A     | issuer server URL                        |
|      | certificate_url           | VARCHAR   | 2000   | YES      | N/A     | issuer certificate VC URL                |
|      | published_certificate_url | VARCHAR   | 2000   | YES      | N/A     | certificate VC URL returned by TA enrollment |
|      | created_at                | TIMESTAMP |        | NO       | NOW()   | created date                             |
|      | updated_at                | TIMESTAMP |        | YES      | N/A     | updated date                             |

### 2.15 ZKP_SCHEMA

| Key  | Column Name | Data Type | Length | Nullable | Default | Description                             |
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

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description                            |
|------|------------------|-----------|--------|----------|---------|----------------------------------------|
| PK   | id               | BIGINT    |        | NO       | N/A     | ID                                      |
|      | label            | VARCHAR   | 100    | NO       | N/A     | Attribute label                         |
|      | type             | VARCHAR   | 50     | NO       | N/A     | Attribute data type                     |
|      | caption          | VARCHAR   | 100    | NO       | N/A     | Display name for UI                     |
|      | created_at       | TIMESTAMP |        | NO       | NOW()   | Creation timestamp                      |
|      | updated_at       | TIMESTAMP |        | YES      | N/A     | Last update timestamp                   |
| FK   | zkp_namespace_id | BIGINT    |        | NO       | N/A     | Related ZKP namespace                   |


### 2.17 ZKP_CREDENTIAL_DEFINITION

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description                             |
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

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description                     |
|------|-----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                              |
|      | tas_url         | VARCHAR   | 2000   | YES      | N/A     | TA Server base URL              |
|      | assert_key_id   | VARCHAR   | 30     | YES      | N/A     | assertion key ID                |
|      | revoke_auth_type| VARCHAR   | 30     | YES      | N/A     | revoke authorization type       |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date                    |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date                    |

### 2.19 ZKP_NAMESPACE

| Key  | Column Name   | Data Type | Length | Nullable | Default | Description                     |
|------|----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | ID                              |
|      | namespace_id   | VARCHAR   | 100    | NO       | N/A     | Unique ZKP namespace ID         |
|      | name           | VARCHAR   | 100    | NO       | N/A     | Namespace name                  |
|      | ref            | VARCHAR   | 200    | YES      | N/A     | Reference URL or information    |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | Creation timestamp              |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | Last update timestamp           |

---

### 2.20 ZKP_SCHEMA_ATTRIBUTE

| Key  | Column Name       | Data Type | Length | Nullable | Default | Description                          |
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

| Key  | Column Name   | Data Type | Length | Nullable | Default | Description                     |
|------|----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A     | ID                              |
|      | did_document   | TEXT      |        | NO       | N/A     | Raw DID Document JSON           |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | Creation timestamp              |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | Last update timestamp           |


### 2.22 ADMIN

| Key  | Column Name              | Data Type                | Length | Nullable | Default | Description                                                      |
|------|--------------------------|--------------------------|--------|----------|---------|------------------------------------------------------------------|
| PK   | id                       | BIGINT                   |        | NO       | N/A     | id                                                               |
|      | login_id                 | VARCHAR                  | 50     | NO       | N/A     | admin login ID                                                   |
|      | login_password           | VARCHAR                  | 64     | NO       | N/A     | hashed login password                                            |
|      | name                     | VARCHAR                  | 200    | YES      | N/A     | admin name                                                       |
|      | email_verified           | BOOLEAN                  |        | NO       | false   | whether email is verified                                        |
|      | require_password_reset   | BOOLEAN                  |        | NO       | true    | require password change on login                                 |
|      | role                     | VARCHAR                  | 50     | NO       | N/A     | admin role                                                       |
|      | created_by               | VARCHAR                  | 50     | NO       | N/A     | creator login ID or system user                                  |
|      | last_password_changed_at | TIMESTAMP WITH TIME ZONE |        | YES      | N/A     | last password change date                                        |
|      | password_reset_reason    | VARCHAR                  | 20     | YES      | N/A     | password reset reason (`FIRST_LOGIN`, `EXPIRED`, `ADMIN_FORCED`) |
|      | created_at               | TIMESTAMP                |        | NO       | NOW()   | created date                                                     |
|      | updated_at               | TIMESTAMP                |        | YES      | N/A     | updated date                                                     |

## 3. OID4VCI and Server Configuration Tables

This section defines the tables added for runtime server configuration, the admin password policy, and OID4VCI issuance (Liquibase changesets `set.3` to `set.8`).

### 3.1 SERVER_CONFIG

Runtime server settings stored as key/value pairs.

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description               |
|------|--------------|-----------|--------|----------|---------|---------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                        |
|      | config_key   | VARCHAR   | 100    | NO       | N/A     | configuration key         |
|      | config_value | TEXT      |        | NO       | N/A     | configuration value       |
|      | description  | VARCHAR   | 200    | YES      | N/A     | configuration description |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date              |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date              |

- Unique constraint: `config_key`
- Initial data: `API_KEY_LSS` (API key sent in the `X-API-Key` header to the Ledger Service Server)
- Keys written at runtime: `OID4VCI_METADATA`, `OID4VCI_AUTH_PROPERTIES`, `OID4VCI_LIST_PROVIDER_REGISTRATION`

### 3.2 ADMIN_PASSWORD_POLICY

Admin password policy.

| Key  | Column Name          | Data Type                | Length | Nullable | Default | Description                        |
|------|----------------------|--------------------------|--------|----------|---------|------------------------------------|
| PK   | id                   | BIGINT                   |        | NO       | N/A     | id                                 |
|      | min_length           | SMALLINT                 |        | NO       | N/A     | minimum password length            |
|      | require_uppercase    | BOOLEAN                  |        | NO       | N/A     | whether an uppercase letter is required |
|      | require_number       | BOOLEAN                  |        | NO       | N/A     | whether a number is required       |
|      | require_special      | BOOLEAN                  |        | NO       | N/A     | whether a special character is required |
|      | password_expiry_days | SMALLINT                 |        | NO       | N/A     | password expiration period (days)  |
|      | created_at           | TIMESTAMP WITH TIME ZONE |        | NO       | N/A     | created date                       |
|      | updated_at           | TIMESTAMP WITH TIME ZONE |        | YES      | N/A     | updated date                       |

- Initial data: one row (`min_length` 8, no character requirements, `password_expiry_days` 365)

### 3.3 CREDENTIAL_CONFIG

OID4VCI Credential Configuration registry.

| Key  | Column Name   | Data Type | Length | Nullable | Default           | Description                                         |
|------|---------------|-----------|--------|----------|-------------------|-----------------------------------------------------|
| PK   | id            | VARCHAR   | 100    | NO       | N/A               | credential configuration ID                         |
|      | format        | VARCHAR   | 50     | NO       | N/A               | credential format (e.g., `dc+sd-jwt`, `mso_mdoc`)   |
|      | doctype       | VARCHAR   | 100    | YES      | N/A               | mDoc docType (not mapped by the JPA entity)         |
|      | identifiers   | VARCHAR   | 200    | YES      | N/A               | credential identifiers (JSON array)                 |
|      | metadata_json | TEXT      |        | YES      | N/A               | credential configuration metadata JSON              |
|      | enabled       | BOOLEAN   |        | YES      | true              | whether the configuration is enabled                |
|      | created_at    | TIMESTAMP |        | YES      | CURRENT_TIMESTAMP | created date                                        |
|      | updated_at    | TIMESTAMP |        | YES      | CURRENT_TIMESTAMP | updated date                                        |

### 3.4 T_OID4VC_OFFER

Credential Offer state.

| Key  | Column Name   | Data Type | Length | Nullable | Default           | Description                        |
|------|---------------|-----------|--------|----------|-------------------|------------------------------------|
| PK   | id            | BIGINT    |        | NO       | N/A               | id                                 |
|      | offer_id      | VARCHAR   | 64     | NO       | N/A               | Credential Offer ID                |
|      | pre_auth_code | VARCHAR   | 128    | YES      | N/A               | pre-authorized code                |
|      | user_pin      | VARCHAR   | 256    | YES      | N/A               | transaction code (user PIN)        |
|      | status        | VARCHAR   | 20     | NO       | N/A               | offer status (e.g., `CREATED`)     |
|      | expires_at    | TIMESTAMP |        | YES      | N/A               | expiration date                    |
|      | created_at    | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                       |
|      | updated_at    | TIMESTAMP |        | YES      | N/A               | updated date                       |

- Unique constraint: `offer_id`

### 3.5 T_OID4VC_CNONCE

Proof nonce (`c_nonce`) storage.

| Key  | Column Name | Data Type | Length | Nullable | Default           | Description                                              |
|------|-------------|-----------|--------|----------|-------------------|----------------------------------------------------------|
| PK   | id          | BIGINT    |        | NO       | N/A               | id                                                       |
|      | cnonce      | VARCHAR   | 128    | NO       | N/A               | c_nonce value                                            |
|      | expires_at  | TIMESTAMP |        | YES      | N/A               | expiration date (10 minutes after creation)              |
|      | consumed_at | TIMESTAMP |        | YES      | N/A               | consumed date; a nonce with a value cannot be reused     |
|      | created_at  | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                             |
|      | updated_at  | TIMESTAMP |        | YES      | N/A               | updated date                                             |

- Unique constraint: `cnonce`

### 3.6 T_OID4VC_PRE_AUTH_CODE

Pre-authorized code state.

| Key  | Column Name | Data Type | Length | Nullable | Default           | Description                       |
|------|-------------|-----------|--------|----------|-------------------|-----------------------------------|
| PK   | id          | BIGINT    |        | NO       | N/A               | id                                |
|      | code        | VARCHAR   | 128    | NO       | N/A               | pre-authorized code               |
|      | user_id     | VARCHAR   | 128    | YES      | N/A               | user ID                           |
|      | user_pin    | VARCHAR   | 256    | YES      | N/A               | transaction code (user PIN)       |
|      | scopes      | TEXT      |        | YES      | N/A               | granted scopes                    |
|      | consumed    | BOOLEAN   |        | YES      | false             | whether the code has been used    |
|      | expires_at  | TIMESTAMP |        | NO       | N/A               | expiration date                   |
|      | created_at  | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                      |
|      | updated_at  | TIMESTAMP |        | YES      | N/A               | updated date                      |

- Unique constraint: `code`

### 3.7 T_OID4VC_SESSION_MAP

Issuance session mapping.

| Key  | Column Name  | Data Type | Length | Nullable | Default           | Description                                         |
|------|--------------|-----------|--------|----------|-------------------|-----------------------------------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A               | id                                                  |
|      | session_key  | VARCHAR   | 128    | NO       | N/A               | session key (issuer state or pre-authorized code)   |
|      | session_type | VARCHAR   | 30     | NO       | N/A               | session type (`ISSUER_STATE`, `PRE_AUTH_CODE_MAP`)  |
|      | user_id      | VARCHAR   | 64     | NO       | N/A               | user ID                                             |
|      | expires_at   | TIMESTAMP |        | YES      | N/A               | expiration date                                     |
|      | created_at   | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                        |
|      | updated_at   | TIMESTAMP |        | YES      | N/A               | updated date                                        |

- Unique constraint: `session_key`

### 3.8 T_OID4VC_DEFERRED_TRANSACTION

Deferred issuance request storage.

| Key  | Column Name    | Data Type | Length | Nullable | Default           | Description                  |
|------|----------------|-----------|--------|----------|-------------------|------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A               | id                           |
|      | transaction_id | VARCHAR   | 128    | NO       | N/A               | deferred transaction ID      |
|      | request_json   | TEXT      |        | NO       | N/A               | credential request JSON      |
|      | expires_at     | TIMESTAMP |        | YES      | N/A               | expiration date              |
|      | created_at     | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                 |
|      | updated_at     | TIMESTAMP |        | YES      | N/A               | updated date                 |

- Unique constraint: `transaction_id`

### 3.9 T_OID4VC_CLIENT

OAuth/OID4VC client registration.

| Key  | Column Name   | Data Type | Length | Nullable | Default           | Description         |
|------|---------------|-----------|--------|----------|-------------------|---------------------|
| PK   | id            | BIGINT    |        | NO       | N/A               | id                  |
|      | client_id     | VARCHAR   | 64     | NO       | N/A               | client ID           |
|      | client_secret | VARCHAR   | 256    | YES      | N/A               | client secret       |
|      | redirect_uri  | VARCHAR   | 512    | YES      | N/A               | redirect URI        |
|      | client_name   | VARCHAR   | 100    | YES      | N/A               | client name         |
|      | created_at    | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date        |
|      | updated_at    | TIMESTAMP |        | YES      | N/A               | updated date        |

- Unique constraint: `client_id`

### 3.10 T_OID4VC_USER_CREDENTIAL_MAP

User-to-credential mapping.

| Key  | Column Name   | Data Type | Length | Nullable | Default           | Description                  |
|------|---------------|-----------|--------|----------|-------------------|------------------------------|
| PK   | id            | BIGINT    |        | NO       | N/A               | id                           |
|      | user_id       | VARCHAR   | 64     | NO       | N/A               | user ID                      |
|      | config_id     | VARCHAR   | 128    | NO       | N/A               | credential configuration ID  |
|      | credential_id | VARCHAR   | 128    | NO       | N/A               | credential ID                |
|      | created_at    | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                 |
|      | updated_at    | TIMESTAMP |        | YES      | N/A               | updated date                 |

### 3.11 T_OID4VC_NOTIFICATION

Wallet notification storage.

| Key  | Column Name       | Data Type | Length | Nullable | Default           | Description             |
|------|-------------------|-----------|--------|----------|-------------------|-------------------------|
| PK   | id                | BIGINT    |        | NO       | N/A               | id                      |
|      | notification_id   | VARCHAR   | 128    | NO       | N/A               | notification ID         |
|      | transaction_id    | VARCHAR   | 128    | NO       | N/A               | transaction ID          |
|      | event_type        | VARCHAR   | 50     | YES      | N/A               | notification event      |
|      | event_description | VARCHAR   | 256    | YES      | N/A               | event description       |
|      | created_at        | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date            |
|      | updated_at        | TIMESTAMP |        | YES      | N/A               | updated date            |

- Unique constraint: `notification_id`

### 3.12 T_OID4VC_USER_CLAIMS

User claims JSON storage.

| Key  | Column Name     | Data Type | Length | Nullable | Default           | Description            |
|------|-----------------|-----------|--------|----------|-------------------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A               | id                     |
|      | user_id         | VARCHAR   | 128    | NO       | N/A               | user ID                |
|      | credential_type | VARCHAR   | 128    | NO       | N/A               | credential type        |
|      | claims          | TEXT      |        | YES      | N/A               | claims JSON            |
|      | created_at      | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A               | updated date           |

- Unique constraint: `uk_user_claims_id_type` (`user_id`, `credential_type`)

### 3.13 T_OID4VC_WEBVIEW_ISSUANCE_SESSION

User-initiated webview issuance session.

| Key  | Column Name                 | Data Type | Length | Nullable | Default           | Description                                                                                        |
|------|-----------------------------|-----------|--------|----------|-------------------|----------------------------------------------------------------------------------------------------|
| PK   | id                          | BIGINT    |        | NO       | N/A               | id                                                                                                 |
|      | session_token               | VARCHAR   | 64     | NO       | N/A               | session token                                                                                      |
|      | csrf_token                  | VARCHAR   | 64     | NO       | N/A               | CSRF token                                                                                         |
|      | user_id                     | VARCHAR   | 128    | NO       | N/A               | user ID                                                                                            |
|      | credential_configuration_id | VARCHAR   | 100    | NO       | N/A               | credential configuration ID                                                                        |
|      | credential_type             | VARCHAR   | 128    | NO       | N/A               | credential type                                                                                    |
|      | status                      | VARCHAR   | 24     | NO       | N/A               | session status (`INPUT_REQUIRED`, `CLAIMS_SAVED`, `OFFER_CREATED`, `FAILED`, `CANCELED`, `EXPIRED`) |
|      | expires_at                  | TIMESTAMP |        | NO       | N/A               | expiration date                                                                                    |
|      | credential_offer_uri        | TEXT      |        | YES      | N/A               | generated Credential Offer URI                                                                     |
|      | created_at                  | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                                                                       |
|      | updated_at                  | TIMESTAMP |        | YES      | N/A               | updated date                                                                                       |

- Unique constraint: `session_token`

### 3.14 T_OID4VC_STATUS_LIST

Status List metadata and index allocation state.

| Key  | Column Name    | Data Type | Length | Nullable | Default           | Description                                              |
|------|----------------|-----------|--------|----------|-------------------|----------------------------------------------------------|
| PK   | id             | BIGINT    |        | NO       | N/A               | id                                                       |
|      | list_uri       | VARCHAR   | 512    | NO       | N/A               | Status List URI                                          |
|      | format         | VARCHAR   | 64     | NO       | N/A               | credential format                                        |
|      | bits           | INTEGER   |        | NO       | 2                 | bits per status entry                                    |
|      | capacity       | BIGINT    |        | NO       | 100000            | maximum number of entries                                |
|      | next_index     | BIGINT    |        | NO       | 0                 | next index to allocate                                   |
|      | signing_key_id | VARCHAR   | 256    | NO       | N/A               | Status List signing key ID (`assert` for auto-created lists) |
|      | ttl_seconds    | BIGINT    |        | NO       | 300               | Status List token TTL (seconds)                          |
|      | list_version   | BIGINT    |        | NO       | 0                 | Status List version                                      |
|      | enabled        | BOOLEAN   |        | NO       | true              | whether the list is enabled                              |
|      | created_at     | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                             |
|      | updated_at     | TIMESTAMP |        | YES      | N/A               | updated date                                             |

- Unique constraint: `list_uri`
- Index: `idx_oid4vc_status_list_format_enabled` (`format`, `enabled`)

### 3.15 T_OID4VC_CREDENTIAL_ISSUANCE

Issued credential lifecycle and allocated Status List index.

| Key  | Column Name       | Data Type | Length | Nullable | Default           | Description                                                    |
|------|-------------------|-----------|--------|----------|-------------------|----------------------------------------------------------------|
| PK   | id                | BIGINT    |        | NO       | N/A               | id                                                             |
|      | issuance_id       | VARCHAR   | 36     | NO       | N/A               | issuance ID (UUID)                                             |
|      | user_id           | VARCHAR   | 128    | NO       | N/A               | user ID                                                        |
|      | subject_did       | VARCHAR   | 512    | YES      | N/A               | Holder DID bound through the proof key; used for revocation    |
|      | config_id         | VARCHAR   | 128    | NO       | N/A               | credential configuration ID                                    |
|      | format            | VARCHAR   | 64     | NO       | N/A               | credential format                                              |
|      | status_list_index | BIGINT    |        | NO       | N/A               | allocated Status List index                                    |
|      | credential_status | VARCHAR   | 20     | NO       | VALID             | credential status (`VALID`, `INVALID`, `SUSPENDED`)            |
|      | issuance_state    | VARCHAR   | 20     | NO       | ALLOCATED         | issuance state (`ALLOCATED`, `ISSUED`, `FAILED`)               |
|      | issued_at         | TIMESTAMP |        | YES      | N/A               | issued date                                                    |
|      | expires_at        | TIMESTAMP |        | YES      | N/A               | expiration date                                                |
|      | status_changed_at | TIMESTAMP |        | YES      | N/A               | last status change date                                        |
|      | token_hash        | VARCHAR   | 64     | YES      | N/A               | SHA-256 hash (hex) of the issued credential                    |
|      | failed_at         | TIMESTAMP |        | YES      | N/A               | issuance failure date                                          |
|      | failure_code      | VARCHAR   | 64     | YES      | N/A               | issuance failure code                                          |
|      | failure_message   | VARCHAR   | 512    | YES      | N/A               | issuance failure message                                       |
|      | created_at        | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                                   |
|      | updated_at        | TIMESTAMP |        | YES      | N/A               | updated date                                                   |
| FK   | status_list_id    | BIGINT    |        | NO       | N/A               | Status List key (`t_oid4vc_status_list.id`)                    |

- Unique constraints: `uk_oid4vc_issuance_id` (`issuance_id`), `uk_oid4vc_status_list_index` (`status_list_id`, `status_list_index`)
- Foreign key: `fk_oid4vc_issuance_status_list` (`status_list_id` -> `t_oid4vc_status_list.id`)
- Indexes: `idx_oid4vc_issuance_user_id` (`user_id`), `idx_oid4vc_issuance_expiry` (`expires_at`), `idx_oid4vc_issuance_subject_did` (`subject_did`)

### 3.16 T_OID4VC_CREDENTIAL_STATUS_HISTORY

Credential status change audit history.

| Key  | Column Name            | Data Type | Length | Nullable | Default           | Description                                        |
|------|------------------------|-----------|--------|----------|-------------------|----------------------------------------------------|
| PK   | id                     | BIGINT    |        | NO       | N/A               | id                                                 |
|      | previous_status        | VARCHAR   | 20     | YES      | N/A               | status before the change                           |
|      | new_status             | VARCHAR   | 20     | NO       | N/A               | status after the change                            |
|      | reason                 | VARCHAR   | 512    | YES      | N/A               | change reason                                      |
|      | changed_by             | VARCHAR   | 128    | NO       | N/A               | actor that changed the status (e.g., `admin-console`) |
|      | changed_at             | TIMESTAMP |        | NO       | N/A               | changed date                                       |
|      | created_at             | TIMESTAMP |        | NO       | CURRENT_TIMESTAMP | created date                                       |
|      | updated_at             | TIMESTAMP |        | YES      | N/A               | updated date                                       |
| FK   | credential_issuance_id | BIGINT    |        | NO       | N/A               | credential issuance key (`t_oid4vc_credential_issuance.id`) |

- Foreign key: `fk_oid4vc_status_history_issuance` (`credential_issuance_id` -> `t_oid4vc_credential_issuance.id`)
- Index: `idx_oid4vc_status_history_issuance_changed` (`credential_issuance_id`, `changed_at`)

### 3.17 T_OID4VC_STATUS_LIST_BOOTSTRAP_LOCK

Singleton lock used when bootstrapping Status Lists.

| Key  | Column Name | Data Type | Length | Nullable | Default | Description                              |
|------|-------------|-----------|--------|----------|---------|------------------------------------------|
| PK   | lock_id     | INTEGER   |        | NO       | N/A     | lock ID (a single row with value `1`)    |

Column constraints are defined by Liquibase changesets and the corresponding JPA entities. Liquibase is the authoritative source for deployed database schemas.
