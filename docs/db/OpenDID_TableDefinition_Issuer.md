
Open DID Issuer Database Table Definition
==

- Date: 2025-03-31
- Version: v1.0.1 (dev)

Contents
--
- [Open DID Issuer Database Table Definition](#open-did-issuer-database-table-definition)
  - [Contents](#contents)
  - [1. Overview](#1-overview)
  - [2. Table Definition](#2-table-definition)
    - [2.1 TRANSACTION](#21-transaction)
    - [2.2 SUB_TRANSACTION](#22-sub_transaction)
    - [2.3 CERTIFIACTE_VC](#23-certifiacte_vc)
    - [2.4 USER](#24-user)
    - [2.5 VC_OFFER](#25-vc_offer)
    - [2.6 VC_PROFILE](#26-vc_profile)
    - [2.7 VC](#27-vc)
    - [2.8 E2E](#28-e2e)
    - [2.9 REVOKE_VC](#29-revoke_vc)
    - [2.10 VC_SCHEMA](#210-vc_schema)
    - [2.11 VC_SCHEMA_NAMESPACE](#211-vc_schema_namespace)
    - [2.12 NAMESPACE](#212-namespace)
    - [2.13 ISSUE_PROFILE](#213-issue_profile)
    - [2.14 ISSUER](#214-issuer)
    - [2.15 APPLICATION_CONFIG](#215-application_config)
    - [2.16 ADMIN](#216-admin)

## 1. Overview

This document defines the structure of the database tables used in the Issuer server. It describes the field attributes, relationships, and data flow for each table, serving as essential reference material for system development and maintenance.

### 1.1 ERD

Access the [ERD](https://www.erdcloud.com/d/d6N5gSY5C4TATnxNR) site to view the diagram, which visually represents the relationships between the tables in the Issuer server database, including key attributes, primary keys, and foreign key relationships.

## 2. Table Definition

### 2.1 TRANSACTION

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | tx_id        | VARCHAR   | 40     | NO       | N/A     | transaction ID         |
|      | type         | VARCHAR   | 50     | NO       | N/A     | transaction type       |
|      | status       | VARCHAR   | 50     | NO       | N/A     | status                 |
|      | vc_plan_id   | VARCHAR   | 20     | YES      | N/A     | VC plan id             |
|      | ref_id       | VARCHAR   | 20     | YES      | N/A     | reference ID           |
|      | expired_at   | TIMESTAMP |        | YES      | N/A     | expiration date        |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date           |

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
| FK  | issue_profile_id | BIGINT    |        | NO       | N/A     | issue profile key |

### 2.3 CERTIFIACTE_VC

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | vc           | TEXT      |        | NO       | N/A     | vc                     |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | create date            |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | update date            |

### 2.4 USER

| Key | Column Name | Data Type | Length | Nullable | Default | Description  |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id           |
|     | pii         | VARCHAR   | 100    | YES      | N/A     | pii          |
|     | data        | TEXT      |        | NO       | N/A     | user data    |
|     | did         | VARCHAR   | 200    | YES      | N/A     | did          |
|     | created_at  | TIMESTAMP |        | NO       | NOW()   | created date |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date |
|     | schema_id   | VARCHAR   | 100    | NO       | N/A     | schema id    |

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
|      | did            | VARCHAR   | 200    | NO       | N/A     | holder DID             |
|      | nonce          | VARCHAR   | 100    | NO       | N/A     | Issuer Nonce           |
|      | created_at     | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id | BIGINT    |        | NO       | N/A     | transaction key        |
| FK   | user_id        | BIGINT    |        | YES      | N/A     | user key               |


### 2.7 VC

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | issued_at    | TIMESTAMP |        | NO       | N/A     | Issued date            |
|      | did          | VARCHAR   | 200    | NO       | N/A     | holder DID             |
|      | vc_id        | VARCHAR   | 40     | NO       | N/A     | VC ID                  |
|      | tx_id        | VARCHAR   | 40     | NO       | N/A     | transaction ID         |
|      | expired_at   | TIMESTAMP |        | NO       | N/A     | expiration date        |
|      | vc_plan_id   | VARCHAR   | 20     | NO       | N/A     | VC plan id             |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | user_id      | BIGINT    |        | NO       | N/A     | User key               |

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
|      | schema_id    | VARCHAR   | 100    | NO       | N/A     | schema id              |
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

| Key    | Column Name   | Data Type | Length | Nullable | Default   | Description             |
| ------ | ------------- | --------- | ------ | -------- | --------- | ----------------------- |
| PK     | id            | BIGINT    |        | NO       | N/A       | id                      |
| PK, FK | vc_schema_id  | BIGINT    |        | NO       | N/A       | VC schema key           |
|        | vc_plan_id    | VARCHAR   | 100    | NO       | N/A       | VC plan id              |
|        | title         | VARCHAR   | 100    | NO       | N/A       | title                   |
|        | description   | VARCHAR   | 200    | YES      | N/A       | description             |
|        | endpoints     | VARCHAR   | 200    | NO       | N/A       | issue API endpoint list |
|        | cipher        | VARCHAR   | 64     | NO       | N/A       | cipher algorithm        |
|        | curve         | VARCHAR   | 64     | NO       | N/A       | curve type              |
|        | padding       | VARCHAR   | 64     | NO       | N/A       | padding method          |
|        | initiate_type | VARCHAR   | 20     | NO       | user init | initiate type           |
|        | language      | VARCHAR   | 10     | NO       | N/A       | language code           |

### 2.14 ISSUER

| Key  | Column Name      | Data Type | Length | Nullable | Default | Description                  |
|------|------------------|-----------|--------|----------|---------|------------------------------|
| PK   | id               | BIGINT    |        | NO       | N/A     | id                           |
|      | did              | VARCHAR   | 200    | NO       | N/A     | issuer DID                   |
|      | name             | VARCHAR   | 200    | NO       | N/A     | issuer name                  |
|      | status           | VARCHAR   | 50     | NO       | N/A     | issuer status                |
|      | server_url       | VARCHAR   | 2000   | YES      | N/A     | issuer server URL            |
|      | certificate_url  | VARCHAR   | 2000   | YES      | N/A     | issuer certificate VC URL    |
|      | created_at       | TIMESTAMP |        | NO       | NOW()   | created date                 |
|      | updated_at       | TIMESTAMP |        | YES      | N/A     | updated date                 |

### 2.15 APPLICATION_CONFIG

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description                     |
|------|-----------------|-----------|--------|----------|---------|---------------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                              |
|      | tas_url         | VARCHAR   | 2000   | YES      | N/A     | TA Server base URL              |
|      | assert_key_id   | VARCHAR   | 30     | YES      | N/A     | assertion key ID                |
|      | revoke_auth_type| VARCHAR   | 30     | YES      | N/A     | revoke authorization type       |
|      | created_at      | TIMESTAMP |        | NO       | N/A     | created date                    |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date                    |

### 2.16 ADMIN

| Key  | Column Name            | Data Type | Length | Nullable | Default | Description                       |
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
