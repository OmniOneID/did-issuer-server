
Open DID Issuer Database Table Definition
==

- Date: 2024-09-04
- Version: v1.0.0

Contents
--
- [Open DID Issuer Database Table Definition](#open-did-issuer-database-table-definition)
  - [Contents](#contents)
  - [1. Overview](#1-overview)
  - [2. Table Definition](#2-table-definition)
    - [2.1 TRANSACTION](#21-transaction)
    - [2.2 SUB\_TRANSACTION](#22-sub_transaction)
    - [2.3 CERTIFIACTE\_VC](#23-certifiacte_vc)
    - [2.4 USER](#24-user)
    - [2.5 VC\_OFFER](#25-vc_offer)
    - [2.6 VC\_PROFILE](#26-vc_profile)
    - [2.7 VC](#27-vc)
    - [2.8 E2E](#28-e2e)
    - [2.9 REVOKE\_VC](#29-revoke_vc)

## 1. Overview

This document defines the structure of the database tables used in the Issuer server. It describes the field attributes, relationships, and data flow for each table, serving as essential reference material for system development and maintenance.

### 1.1 ERD

Access the [ERD](https://www.erdcloud.com/d/BZwNAdDwyzf95GjFr) site to view the diagram, which visually represents the relationships between the tables in the Issuer server database, including key attributes, primary keys, and foreign key relationships.

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

| Key  | Column Name     | Data Type | Length | Nullable | Default | Description            |
|------|-----------------|-----------|--------|----------|---------|------------------------|
| PK   | id              | BIGINT    |        | NO       | N/A     | id                     |
|      | step            | INTEGER   |        | NO       | N/A     | step                   |
|      | type            | VARCHAR   | 50     | NO       | N/A     | transaction type       |
|      | status          | VARCHAR   | 50     | NO       | N/A     | status                 |
|      | created_at      | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date           |
| FK   | transaction_id  | BIGINT    |        | NO       | N/A     | transaction key        |

### 2.3 CERTIFIACTE_VC

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | vc           | TEXT      |        | NO       | N/A     | vc                     |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | create date            |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | update date            |

### 2.4 USER

| Key  | Column Name  | Data Type | Length | Nullable | Default | Description            |
|------|--------------|-----------|--------|----------|---------|------------------------|
| PK   | id           | BIGINT    |        | NO       | N/A     | id                     |
|      | pii          | VARCHAR   | 100    | YES      | N/A     | pii                    |
|      | data         | TEXT      |        | NO       | N/A     | user data              |
|      | did          | VARCHAR   | 200    | YES      | N/A     | did                    |
|      | created_at   | TIMESTAMP |        | NO       | NOW()   | created date           |
|      | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date           |

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
