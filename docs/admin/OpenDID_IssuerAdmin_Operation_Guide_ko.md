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

OpenDID IssuerAdmin Operation Guide
==

- Date: 2025-05-29
- Version: v2.0.0

개정 이력
==
| 버전   | 일자       | 변경 내용                  |
| ------ | ---------- | -------------------------- |
| v1.0.0 | 2025-03-31 | 최초 작성                  |
| v1.0.1 | 2025-04-25 | `3.1. Issuer Registration` 장 추가 <br> `3.5. Issued VC Management` 장 수정 |
| v2.0.0 | 2025-05-29 |  `3.4. ZKP Management` 장 추가 |

목차
==
- [OpenDID IssuerAdmin Operation Guide](#opendid-issueradmin-operation-guide)
- [개정 이력](#개정-이력)
- [목차](#목차)
- [1. 소개](#1-소개)
  - [1.1. 개요](#11-개요)
  - [1.2. Admin Console 정의](#12-admin-console-정의)
- [2. 기본 메뉴얼](#2-기본-메뉴얼)
  - [2.1. 로그인](#21-로그인)
  - [2.2. 메인 화면 구성](#22-메인-화면-구성)
  - [2.3. 메뉴 구성](#23-메뉴-구성)
    - [2.3.1. Issuer 미등록 상태](#231-issuer-미등록-상태)
    - [2.3.3. Issuer 등록 상태](#233-issuer-등록-상태)
  - [2.4. 비밀번호 변경 관리](#24-비밀번호-변경-관리)
- [3. 기능별 상세 메뉴얼](#3-기능별-상세-메뉴얼)
  - [3.1. Issuer Registration](#31-issuer-registration)
    - [▸ Step 1 – Enter Issuer Info](#-step-1--enter-issuer-info)
    - [▸ Step 2 – Register DID Document](#-step-2--register-did-document)
    - [▸ Step 3 – Register Entity and Issue Certificate VC](#-step-3--register-entity-and-issue-certificate-vc)
    - [▸ 등록 완료 화면](#-등록-완료-화면)
  - [3.2. Issuer Management](#32-issuer-management)
    - [▸ Issuer Manegement](#-issuer-manegement)
  - [3.3. VC Management](#33-vc-management)
  - [3.3.1. Namespace Management](#331-namespace-management)
    - [▸ Namespace 목록](#-namespace-목록)
    - [▸ Namespace 등록](#-namespace-등록)
    - [▸ Namespace 상세 정보](#-namespace-상세-정보)
    - [▸ Namespace 수정 (Update)](#-namespace-수정-update)
  - [3.3.2. VC Schema Management](#332-vc-schema-management)
    - [▸ VC Schema 목록](#-vc-schema-목록)
    - [▸ VC Schema 등록](#-vc-schema-등록)
    - [▸ VC Schema 상세 정보](#-vc-schema-상세-정보)
  - [3.3.3. Issue Profile Management](#333-issue-profile-management)
    - [사용 흐름](#사용-흐름)
    - [▸ Issue Profile 목록](#-issue-profile-목록)
    - [▸ Issue Profile 등록](#-issue-profile-등록)
    - [▸ Issue Profile 상세 정보](#-issue-profile-상세-정보)
    - [▸ Issue Profile 수정 (Update)](#-issue-profile-수정-update)
  - [3.4. ZKP Management](#34-zkp-management)
    - [▸ ZKP VC 발급 흐름 요약](#-zkp-vc-발급-흐름-요약)
  - [3.4.1. ZKP Namespace Management](#341-zkp-namespace-management)
    - [▸ Namespace 목록](#-namespace-목록-1)
    - [▸ Namespace 등록](#-namespace-등록-1)
    - [▸ Namespace 상세 정보](#-namespace-상세-정보-1)
    - [▸ Namespace 수정](#-namespace-수정)
  - [3.4.2. Credential Schema Management](#342-credential-schema-management)
    - [일반 VC Schema와 ZKP Credential Schema의 차이점](#일반-vc-schema와-zkp-credential-schema의-차이점)
    - [▸ Credential Schema 목록](#-credential-schema-목록)
    - [▸ Credential Schema 등록](#-credential-schema-등록)
    - [▸ Credential Schema 상세 정보](#-credential-schema-상세-정보)
  - [3.4.3. Credential Definition Management](#343-credential-definition-management)
    - [VC 발급 시 ZKP Credential 생성 및 사용자 전달](#vc-발급-시-zkp-credential-생성-및-사용자-전달)
    - [Credential Definition 구성 요소](#credential-definition-구성-요소)
    - [▸ Credential Definition 목록](#-credential-definition-목록)
    - [▸ Credential Definition 등록](#-credential-definition-등록)
    - [▸ Credential Definition 상세 정보](#-credential-definition-상세-정보)
  - [3.5. User Management](#35-user-management)
    - [▸ 사용자 목록](#-사용자-목록)
    - [▸ 사용자 상세 정보](#-사용자-상세-정보)
  - [3.6. Issued VC Management](#36-issued-vc-management)
    - [▸ Issued VC 목록](#-issued-vc-목록)
    - [▸ Issued VC Status 변경](#-issued-vc-status-변경)
    - [▸ Issued VC 상세 정보](#-issued-vc-상세-정보)
  - [3.7. Admin Management](#37-admin-management)
    - [3.7.1 Admin 목록 조회](#371-admin-목록-조회)
    - [3.7.2. Admin 등록](#372-admin-등록)

---

# 1. 소개

## 1.1. 개요

본 문서는 Open DID Issuer Admin Console의 설치 및 구동 방법을 안내합니다.
기본 사용법부터 각 기능별 상세 매뉴얼까지 단계적으로 설명하여, 사용자가 콘솔을 효율적으로 활용할 수 있도록 구성되어 있습니다.

OpenDID의 전체 설치에 대한 가이드는 [Open DID Installation Guide]를 참고해 주세요.

## 1.2. Admin Console 정의

**Issuer Admin Console**은 Open DID 시스템 내에서 **Issuer 서버**를 관리하기 위한 웹 기반의 관리자 도구입니다.

Issuer는 Verifiable Credential(VC)의 발급 주체로서, VC 발급과 관련된 네임스페이스, 스키마, 발급 정책(프로파일) 등의 관리 기능을 제공합니다.

Issuer Admin Console에서 설정할 수 있는 주요 항목은 다음과 같습니다:

- **Issuer 기본 정보 관리**
  - Issuer 서버 등록 및 상태 확인
- **VC 발급 항목 관리**
  - Namespace 등록 및 항목 구성
  - VC Schema 등록 및 관리
  - 발급 정책(Profile) 설정
- **ZKP 발급 항목 관리**
  - Namespace 등록 및 항목 구성
  - Credential Schema 등록
  - Credential Definition 등록
- **사용자 및 VC 이력 관리**
  - 발급 대상 사용자 관리
  - 발급된 VC 목록 확인 및 상태 추적

# 2. 기본 메뉴얼

이 장에서는 Open DID Issuer Admin Console의 기본적인 사용 방법에 대해 안내합니다.

## 2.1. 로그인

Issuer Admin Console에 접속하려면 다음 단계를 따르세요:

1. 웹 브라우저를 열고 Issuer Admin Console URL에 접속합니다.

   ```
   http://<issuer_domain>:<port>
   ```

2. 로그인 화면에서 관리자 계정의 이메일과 비밀번호를 입력합니다.
   - 기본 관리자 계정: <admin@opendid.omnione.net>
   - 초기 비밀번호: password (최초 로그인 시 변경 필요)

3. '로그인' 버튼을 클릭합니다.

> **참고**:  
> 보안상의 이유로 최초 로그인 시에는 비밀번호 변경이 필요합니다.

<br/>


## 2.2. 메인 화면 구성

로그인 후 표시되는 메인 화면은 다음과 같은 요소들로 구성되어 있습니다:

<img src="./images/2-1.main-screen.png" width="600"/>

| 번호 | 영역             | 설명                                                                                                                                |
| ---- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| 1    | 헤더 영역        | 우측 상단의 `SETTING` 버튼을 통해 비밀번호 변경 화면으로 이동할 수 있습니다.                                                        |
| 2    | 콘텐츠 영역      | 현재 선택된 메뉴의 제목과 해당 콘텐츠가 표시됩니다. 각 메뉴에 따라 화면 내용이 바뀝니다.                                            |
| 3    | 사이드 메뉴      | 화면 왼쪽에 위치하며, 주요 메뉴 항목들이 세로로 정렬되어 있습니다. 선택한 메뉴는 강조 표시되며, 필요한 경우 하위 메뉴가 펼쳐집니다. |
| 4    | 사용자 정보 영역 | 현재 로그인한 관리자의 이메일 주소와 '로그아웃(Sign Out)' 버튼이 표시됩니다.                                                        |


<br/>

## 2.3. 메뉴 구성

Issuer Admin Console의 사이드바 메뉴는 **Issuer 등록 상태에 따라 화면 구성에 차이**가 있습니다.


<br/>

### 2.3.1. Issuer 미등록 상태

Issuer 서버가 아직 등록되지 않은 초기 상태에서는
메뉴에 Issuer Registration 항목만 단독으로 표시됩니다.

<img src="./images/2-3-1.issuer-menu-before-registration.png" width="250"/>

> 참고: Issuer 등록이 완료되면 관련 기능들이 활성화되며, 전체 메뉴가 확장됩니다.
등록 이후 메뉴 구성에 대한 자세한 내용은 추후 항목에서 설명합니다.

### 2.3.3. Issuer 등록 상태

Issuer 등록이 완료되면 전체 관리 기능이 활성화되며, 사이드바 메뉴는 다음과 같이 구성됩니다:

<img src="./images/2-3-2.issuer-menu-after-registration.png" width="260"/>

| 번호 | 메뉴 명칭 | Depth | 설명 |
|------|-----------|--------|------|
| 1 | **Issuer Management** | 1 | Issuer 서버의 기본 정보(DID, 상태 등)를 확인하고 관리하는 메뉴입니다. |
| 2 | **VC Management** | 1 | VC 발급과 관련된 항목들을 설정할 수 있는 상위 메뉴입니다. |
| 3 | └ Namespace Management | 2 | VC 발급에 사용되는 namespace를 등록하고 관리하는 메뉴입니다. |
| 4 | └ VC Schema Management | 2 | VC 발급에 사용될 VC 스키마를 등록하고 관리하는 메뉴입니다. |
| 5 | └ Issue Profile Management | 2 | VC 발급 시 필요한 프로파일 정보를 관리하는 메뉴입니다. |
| 6 | **ZKP Management** | 1 | ZKP 발급과 관련된 항목들을 설정할 수 있는 상위 메뉴입니다. |
| 7 | └ ZKP Namespace Management | 2 | Credential Schema에 사용되는 namespace를 등록하고 관리하는 메뉴입니다. |
| 8 | └ Credential Schema Management | 2 | Credential Defitnition에 사용될 Credential 스키마를 등록하고 관리하는 메뉴입니다. |
| 9 | └ Credential Definition Management | 2 | Definition 발급에 사용될 Credential Definition을 등록하고 관리하는 메뉴입니다. |
| 10 | **User Management** | 1 | VC를 발급받을 사용자 정보를 관리하는 메뉴입니다. |
| 11 | **Issued VC Management** | 1 | 발급된 VC의 목록을 확인하고 관리할 수 있는 메뉴입니다. |

> **참고**:  
> 위 메뉴 구성에 대한 각 기능의 상세 사용법은  
> [3장. 기능별 상세 메뉴얼](#3-기능별-상세-메뉴얼)에서 번호 순서에 따라 설명합니다.
<br/>

## 2.4. 비밀번호 변경 관리

사용자 비밀번호 변경은 다음 단계를 통해 수행할 수 있습니다:

1. 헤더 영역의 'SETTING' 버튼을 클릭합니다.
2. 설정 메뉴에서 '비밀번호 변경'을 선택합니다.
3. 비밀번호 변경 화면에서:
   - 현재 비밀번호 입력
   - 새 비밀번호 입력
   - 새 비밀번호 확인 입력
4. '저장' 버튼을 클릭하여 변경 사항을 적용합니다.

> **참고**: 비밀번호는 8자 이상, 64자 이하의 알파벳 대/소문자, 숫자, 특수문자를 포함해야 합니다.

<br/>

# 3. 기능별 상세 메뉴얼

이 장에서는 Issuer Admin Console의 주요 기능에 대한 상세 사용 방법을 안내합니다.

> **중요 안내**
> Issuer Admin Console의 주요 기능(VC 발급 관련 설정 포함)은 **Issuer Registration**을 먼저 완료한 경우에만 사용할 수 있습니다.
> Issuer 등록이 완료되어야 시스템 내 모든 VC 관련 관리 메뉴가 활성화되며, 발급 대상 VC 구성 또한 설정할 수 있습니다.

또한, VC를 실제로 발급하기 위해서는 다음의 절차를 선행적으로 수행해야 합니다.

* **일반 VC 발급을 위한 기본 구성 절차**
  
  1. **Namespace 등록** – VC에 포함될 Claim 항목 정의
  2. **VC Schema 등록** – Namespace 기반 VC 데이터 구조 정의
  3. **Issue Profile 등록** – 발급 방식 및 정책을 구성하는 발급 프로파일 등록

* **ZKP 기반 VC 발급을 위한 추가 구성 절차**

  1. **ZKP Namespace 등록** – ZKP 방식에 특화된 Claim 구성
  2. **Credential Schema 등록** – ZKP 방식에 맞춘 VC 스키마 정의
  3. **Credential Definition 등록** – ZKP 네트워크와 연결된 Definition 정의
  4. **Issue Profile 등록 시 ZKP 설정 포함** – 해당 Definition ID를 기반으로 ZKP 발급 활성화

위 설정이 완료된 이후에야 사용자 등록 및 VC 발급이 가능하며, 잘못된 순서로 접근할 경우 VC 발급 실패나 UI 비활성 등의 오류가 발생할 수 있습니다.

> **참고**
> 모든 VC 관련 구성은 **Issuer 등록 이후** 진행할 수 있으며, 시스템은 선행 항목의 존재 여부를 기준으로 화면 내 선택 항목 또는 버튼을 제한하거나 오류 메시지를 표시합니다.


## 3.1. Issuer Registration

Issuer Registration은 Issuer Admin Console을 통해 발급 주체인 Issuer를 등록하기 위한 절차입니다. 전체 등록은 총 3단계로 구성되며, 각 단계별로 입력, 요청, 승인 과정을 거쳐 등록이 완료됩니다.
모든 단계가 완료되면 Issuer는 OpenDID 네트워크에 정식 등록됩니다.

<br/>

###  ▸ Step 1 – Enter Issuer Info

<img src="./images/3-1-1.issuer-registration-step1.png" width="700"/>

Issuer의 이름과 서버 URL을 입력하는 단계입니다.

- **Name**: 등록할 Issuer의 이름  
- **Issuer URL**: `http://{IP}:8091/issuer` 형식의 서버 엔드포인트  

> **Note**  
> Issuer 서버와 Admin Console은 같은 Base URL을 사용하며, 동일한 애플리케이션 내에서 패키지로 분리되어 있습니다.

- 모든 항목 입력 후 **NEXT** 버튼을 클릭하여 다음 단계로 이동합니다.

<br/>

### ▸ Step 2 – Register DID Document

Issuer의 DID Document를 생성하고, TAS(Trust Agent Service)에 등록 요청 및 승인을 받는 단계입니다. 아래 순서를 따릅니다:

<br/>

**1. Generate DID Document**  
<img src="./images/3-1-1.issuer-registration-step2-1.png" width="500"/>  
`GENERATE` 버튼을 클릭하면 DID 문서가 자동 생성되며, 화면에 JSON 형식으로 출력됩니다.  
> 생성 완료 메시지: ✅ DID Document has been successfully created.

<br/>

**2. Submit Registration Request**  
<img src="./images/3-1-1.issuer-registration-step2-2.png" width="500"/>  
`REQUEST` 버튼 클릭 시 TAS에 DID 문서 등록 요청이 전송됩니다.  
> 요청 완료 메시지: ✅ Registration request has been submitted.

<br/>

**3. Check Approval Status**  
<img src="./images/3-1-1.issuer-registration-step2-3.png" width="500"/>  
`CHECK` 버튼을 클릭하여 TAS 관리자의 승인을 확인합니다.  
> 승인 완료 메시지: ✅ Approval confirmed. You can proceed.

<br/>

**4. 모든 과정을 완료한 화면**  
<img src="./images/3-1-1.issuer-registration-step2-4.png" width="500"/>

<br/>

### ▸ Step 3 – Register Entity and Issue Certificate VC

<img src="./images/3-1-1.issuer-registration-step3.png" width="700"/>

Issuer를 OpenDID 네트워크의 엔터티로 등록하고, **Certificate VC(신뢰 증명용 VC)**를 발급받는 마지막 단계입니다.

- `REQUEST` 버튼을 클릭하면 TAS를 통해 Entity가 등록됩니다.
- 등록이 완료되면 `FINISH` 버튼을 클릭하여 전체 등록을 마무리합니다.

> **참고**  
> Certificate VC는 OpenDID 구성 요소 간의 신뢰 관계를 증명하는 자격 증명입니다.

<br/>

### ▸ 등록 완료 화면

모든 단계를 성공적으로 마치면 아래와 같이 완료 화면이 표시됩니다.

<img src="./images/3-1-1.issuer-registration-completed.png" width="700"/>

- "Completed" 메시지가 나타나며, `GO TO HOME` 버튼을 클릭하여 Admin Console 메인 화면으로 이동할 수 있습니다.


## 3.2. Issuer Management

Issuer Management는 Issuer 서버의 기본 정보를 등록하고 관리하는 메뉴입니다. Issuer는 VC(Verifiable Credential)의 발급 주체로서 시스템 내에서 고유한 DID를 가지고 등록되어야 하며, 최초 1회만 등록이 필요합니다.  

Issuer가 등록되면 시스템에 활성 상태(`ACTIVATE`)로 표시되며, 등록 이후에는 등록된 정보를 열람하거나 제한된 범위 내에서 변경할 수 있습니다.

### ▸ Issuer Manegement

> **참고**  
현재는 Quick Register 방식의 간편 등록만 지원하며, 정식 등록 절차는 2025년 6월에 업데이트될 예정입니다.

Issuer Manegement 화면은 다음 항목들로 구성되어 있습니다.

<img src="./images/3-2-1.issuer-management-info.png" width="350"/>

| 항목              | 설명                                                           |
|-------------------|----------------------------------------------------------------|
| **DID**           | Issuer를 식별하는 고유한 DID로, 시스템에서 자동 발급됩니다.     |
| **Name**          | 등록할 Issuer의 이름입니다.                                    |
| **Status**        | Issuer의 상태를 나타냅니다. 등록 완료 시 `ACTIVATE`로 표시됩니다. |
| **URL**           | Issuer 서버의 기본 URL입니다.                                  |
| **Certificate URL** | Issuer의 가입증명서를 확인할 수 있는 URL 주소입니다.          |
| **Registered At** | Issuer의 최초 등록 일시입니다.                                 |

- DID Document는 화면의 `VIEW DID DOCUMENT` 버튼을 통해 확인할 수 있습니다.
- 등록된 Issuer 정보는 삭제가 불가능하며, 일부 항목에 대해 제한된 범위 내에서만 수정이 가능합니다.

## 3.3. VC Management

VC Management는 Issuer가 발급하는 VC(Verifiable Credential)를 정의하고 관리하기 위한 메뉴입니다. VC를 발급하기 위해서는 사전에 Namespace와 VC Schema를 등록하여 VC 구조를 정의하고, 발급 정책(Issue Profile)을 설정해야 합니다.

VC Management는 다음 세 가지 하위 메뉴로 구성되어 있습니다.

- Namespace Management
- VC Schema Management
- Issue Profile Management

---

## 3.3.1. Namespace Management

Namespace Management는 VC의 구조를 정의할 때 사용되는 네임스페이스(Namespace)를 등록하고 관리하는 메뉴입니다.  
Namespace는 **하나의 VC에 포함될 Claim(속성) 항목들을 그룹화한 논리적 단위**로, 각 항목은 `ID`, `Type`, `Format`, `Caption` 정보를 포함합니다.

등록된 Namespace는 이후 VC Schema를 생성할 때 포함 항목으로 선택되어 **VC의 데이터 구조를 구성하는 기반**이 됩니다. 또한, Issue Profile 생성 시에도 간접적으로 참조되며,  
**사용자 VC 발급 시 입력해야 할 항목들의 기준을 정의하는 핵심 요소**입니다.

즉, Namespace는 전체 VC 발급 시스템에서 다음과 같은 흐름으로 활용됩니다:

1. **Namespace 등록** – VC에 포함될 항목 정의  
2. **VC Schema 생성 시 Namespace를 선택하여 구조 정의**  
3. **Issue Profile 등록 시 VC Schema를 참조**  
4. **사용자 등록 시 VC 데이터(JSON) 입력 기준으로 활용**

> 본 메뉴는 **Issuer 등록 후 사용 가능**합니다. VC Schema 및 Issue Profile 등록 시 참조되므로, **최초로 구성되어야 할 항목**입니다.

### ▸ Namespace 목록

Namespace 목록 화면에서는 등록된 네임스페이스들을 확인할 수 있습니다.

<img src="./images/3-3-1.namespace-management.png" width="700"/>

| 항목                  | 설명                                |
|---------------------|-----------------------------------|
| **ID**              | 네임스페이스의 고유 식별자입니다.                |
| **Name**            | 네임스페이스 이름입니다. 클릭 시 상세 화면으로 이동합니다. |
| **VC Schema Count** | 네임스페이스를 참조하는 VC Schema의 수 입니다.    |
| **Registered At**   | 네임스페이스가 등록된 일시입니다.                |

- `REGISTER` 버튼을 클릭하면 네임스페이스를 새로 등록할 수 있습니다.
- 네임스페이스를 선택한 후 `UPDATE` 버튼을 클릭하면 해당 네임스페이스를 수정할 수 있습니다.
   >네임스페이스를 사용하는 VC Schema가 없을 경우 수정할 수 있습니다.
  >VC Schema에서 해당 네임스페이스를 참조하고 있을 경우 수정할 수 없습니다.


### ▸ Namespace 등록

네임스페이스 등록 화면에서는 다음 정보를 입력하여 네임스페이스를 생성합니다.

<img src="./images/3-3-1.namespace-registration.png" width="500"/>

| 항목            | 설명                                              |
|-----------------|---------------------------------------------------|
| **Namespace ID**| 네임스페이스의 고유 식별자(예: `iso.18013.5`)입니다.|
| **Name**        | 네임스페이스 이름입니다.                           |
| **Ref**         | 네임스페이스와 관련된 참조 URL 또는 설명입니다.      |
| **Items**       | 네임스페이스에 포함될 항목입니다. 다음 필드로 구성됩니다:<br>-`ID`, `Type`, `Format`, `Caption`|

- `ADD ITEM` 버튼으로 항목을 추가하며, 항목 삭제 및 수정도 가능합니다.
- 입력 후 `REGISTER` 버튼을 클릭하여 등록합니다.

### ▸ Namespace 상세 정보

네임스페이스 목록에서 이름을 클릭하면 상세 정보를 확인할 수 있습니다.

<img src="./images/3-3-1.namespace-detail.png" width="500"/>

| 항목            | 설명                             |
|-----------------|----------------------------------|
| **Namespace ID**| 네임스페이스 고유 식별자입니다.  |
| **Name**        | 네임스페이스 이름입니다.         |
| **Ref**         | 참조 URL 또는 설명입니다.        |
| **Items**       | 등록된 항목 목록입니다. 각 항목은 ID, Type, Format, Caption으로 구성됩니다.|

### ▸ Namespace 수정 (Update)

네임스페이스의 기존 정보를 수정할 수 있는 화면입니다.

<img src="./images/3-3-1.namespace-update.png" width="500"/>

| 항목             | 설명                                   |
|------------------|----------------------------------------|
| **Namespace ID** | 고유 식별자이므로 수정할 수 없습니다.  |
| **Name**         | 네임스페이스 이름을 수정할 수 있습니다. |
| **Ref**          | 참조 URL 또는 설명을 수정할 수 있습니다.|
| **Items**        | 등록된 항목을 추가, 수정, 삭제할 수 있습니다.|

- 변경 사항 수정 후 `UPDATE` 버튼을 클릭하여 저장합니다.
- `RESET` 버튼은 폼을 초기 상태로 되돌리며, `BACK` 버튼으로 상세보기 화면으로 이동할 수 있습니다.

---

## 3.3.2. VC Schema Management

VC Schema Management는 Verifiable Credential(VC)의 데이터 구조를 정의하는 **VC Schema**를 등록하고 관리하는 메뉴입니다.

VC Schema는 **VC 내에 포함될 데이터 필드(Claim)의** 구성과 형식을 정의하는 스키마 문서로,  
사전에 등록한 **Namespace에 정의된 Claim 항목들을 조합하여 생성**됩니다.  
즉, VC Schema는 Namespace를 기반으로 만들어지며, **어떤 정보가 VC에 담길지 명확히 규정**합니다.

등록된 VC Schema는 이후 **Issue Profile 생성 시 참조**되며,  
발급 대상 사용자 정보를 입력할 때는 이 Schema를 기준으로 VC JSON 구조가 결정됩니다.  
이로 인해 VC Schema는 **VC 발급의 핵심 기준점 역할**을 하며, 다음과 같은 흐름으로 사용됩니다:

1. **Namespace 등록** – Claim 항목 정의  
2. **VC Schema 등록** – Namespace를 기반으로 VC 구조 정의  
3. **Issue Profile 등록 시 VC Schema 선택**  
4. **사용자 등록 시 Schema 기준으로 JSON 데이터 입력**  
5. **VC 발급 시 Schema를 기반으로 실제 VC 생성**

> 본 메뉴는 **Issuer 등록 및 Namespace 선행 등록 후 사용 가능합니다.**  
> VC 발급을 위한 구조 정의의 중심이 되는 항목으로, **정확한 Claim 구성과 스키마 설계가 중요**합니다.

### ▸ VC Schema 목록

등록된 VC Schema 목록을 확인합니다.

<img src="./images/3-3-2.vc-schema-management.png" width="700"/>

| 항목            | 설명                             |
|-----------------|----------------------------------|
| **ID**          | VC Schema의 고유 식별자입니다.   |
| **Title**       | 스키마의 제목입니다. 클릭 시 상세 화면으로 이동합니다.|
| **Registered At**| 최초 등록된 일시입니다.          |
| **Updated At**  | 마지막 수정된 일시입니다.         |

- `REGISTER` 버튼을 클릭하여 새 VC Schema를 등록할 수 있습니다.

### ▸ VC Schema 등록

새로운 VC Schema를 등록합니다.

<img src="./images/3-3-2.vc-schema-registration.png" width="700"/>

| 항목            | 설명                                        |
|-----------------|---------------------------------------------|
| **VC Schema ID**| VC Schema의 고유 식별자 또는 URL입니다.      |
| **Title**       | 스키마의 제목입니다.                         |
| **Language**    | VC Schema 언어(예: `ko`)입니다.                   |
| **Version**     | 스키마 버전입니다.                           |
| **Description** | VC Schema에 대한 설명입니다.                 |
| **Items**       | Schema에 포함될 네임스페이스 기반 항목 목록입니다.|

- 입력 완료 후 `REGISTER` 버튼으로 등록합니다.

---

### ▸ VC Schema 상세 정보

VC Schema 목록에서 제목을 클릭하면 스키마의 상세 정보를 확인할 수 있습니다.

<img src="./images/3-3-2.vc-schema-detail.png" width="700"/>

| 항목                 | 설명                                                    |
|----------------------|---------------------------------------------------------|
| **VC Schema ID**     | VC Schema를 식별하는 고유한 URL 또는 ID입니다.          |
| **Title**            | VC Schema의 제목입니다.                                 |
| **Language**         | 스키마 설명 언어 설정입니다. (예: `ko`)                 |
| **Version**          | 스키마의 버전 정보입니다.                               |
| **Description**      | 스키마에 대한 상세 설명입니다.                          |
| **Credential Subject** | VC Schema에 포함된 Namespace 목록입니다. 항목을 클릭하면 해당 Namespace 상세 정보를 확인할 수 있습니다. |

---

## 3.3.3. Issue Profile Management

Issue Profile Management는 Verifiable Credential(VC) 발급을 위한 **발급 프로파일(Issue Profile)을** 정의하고 관리하는 메뉴입니다.

Issue Profile은 발급 대상 VC의 스키마(VC Schema)와 발급 방식(예: User/Issuer Initiate), 보안 설정 등을 하나의 단위로 묶은 **VC 발급 정책**입니다.  

VC를 실제로 발급하려면 반드시 해당 VC Schema를 기반으로 한 Issue Profile이 등록되어 있어야 하며,  
VC 발급 시 시스템은 이 Profile을 참조하여 발급 절차, 구조, 보안 설정을 자동 구성합니다.

Issue Profile은 다음과 같은 요소로 구성됩니다:

- 참조할 VC Schema ID
- VC 발급 방식 (`User Initiate`, `Issuer Initiate`)
- 발급 시 사용할 Endpoint 주소 목록
- E2E(End-to-End) 암호화 설정
- (선택) ZKP 발급 여부 및 Credential Definition ID

> **ZKP Credential을 발급하고자 하는 경우**, 반드시 ZKP Credential Definition이 사전에 등록되어 있어야 하며,  
Issue Profile 생성 시 해당 Definition ID를 연결해야 ZKP Credential을 포함해서 발급받을 수 있습니다.

---

### 사용 흐름

1. **Namespace 등록** → Claim 정의  
2. **VC Schema 등록** → 구조 정의  
3. **Credential Definition 등록 (ZKP 발급 시)**  
4. **Issue Profile 등록** → VC 발급 정책 정의  
5. **User 등록 및 VC 발급**

> 하나의 VC Schema에 대해 여러 Issue Profile을 정의할 수 있으며,  
> 사용 목적에 따라 발급 방식과 보안 설정을 달리 구성할 수 있습니다. (예: 일반 사용자용 vs 관리자용)

> 본 메뉴는 **Issuer 등록, Namespace 및 VC Schema 등록이 완료된 후 사용 가능합니다.**  
> ZKP 기반 VC를 발급하려면 Credential Definition 등록 또한 선행되어야 합니다.


### ▸ Issue Profile 목록

등록된 Issue Profile 목록을 확인할 수 있습니다.

<img src="./images/3-3-3.issue-profile-management.png" width="1400"/>

| 항목            | 설명                                             |
|-----------------|--------------------------------------------------|
| **VC Plan ID**  | Issue Profile을 식별하는 고유한 ID입니다. 클릭 시 상세 화면으로 이동합니다.|
| **Title**       | 프로파일의 제목입니다.                            |
| **VC Schema ID**| 연결된 VC Schema의 ID입니다. 클릭 시 Schema 상세 화면으로 이동합니다.|
| **Registered At**| Issue Profile이 등록된 일시입니다.               |

- `REGISTER` 버튼을 클릭하여 새로운 Issue Profile을 등록할 수 있습니다.


---

### ▸ Issue Profile 등록

<img src="./images/3-3-3.issue-profile-registration.png" width="400"/>

| 항목               | 설명                                                                 |
|--------------------|----------------------------------------------------------------------|
| **VC Plan ID**     | 발급 프로파일의 고유 식별자입니다.                                   |
| **Title**          | 발급 프로파일의 제목입니다.                                           |
| **Description**    | 프로파일의 상세 설명입니다. (선택 사항)                              |
| **VC Schema ID**   | 연동할 VC Schema의 ID입니다. (돋보기 버튼으로 선택 가능)              |
| **Initiate Type**  | VC 발급 요청의 시작 방식 설정<br/>(예: User Initiate, Issuer Initiate)|
| **Language**       | 프로파일의 언어 설정입니다. (예: `ko`)                                |
| **Endpoints**      | VC 발급 요청을 수신할 Endpoint 주소 목록입니다.                       |
| **E2E**            | End-to-End 암호화 구성<br/>- Cipher, Curve, Padding 설정 가능          |
| **Tags**           | 해당 프로파일을 구분하거나 검색 시 활용할 태그 정보입니다.             |
| **ZKP 발급 여부**  | `Issuer Initiate` 방식인 경우, ZKP 기반 발급 여부를 설정할 수 있습니다. |
| **Credential Definition ID** | ZKP 발급 시 참조할 Credential Definition ID를 입력합니다.     |

- `ZKP 발급 여부` 스위치를 켜면 Definition ID 입력 필드가 활성화됩니다.
- 모든 항목 입력 후 `REGISTER` 버튼 클릭 시 프로파일이 등록됩니다.  
- `RESET` 버튼은 폼을 초기화하고, `CANCEL`은 등록을 취소합니다.

---

### ▸ Issue Profile 상세 정보

<img src="./images/3-3-3.issue-profile-detial.png" width="400"/>

- 등록된 Issue Profile의 상세 정보를 확인할 수 있습니다.
- `ZKP 발급 여부`가 설정된 경우, 관련 Definition ID가 하단에 표시됩니다.

| 항목                     | 설명                                                  |
|--------------------------|-------------------------------------------------------|
| **VC Plan ID**           | 발급 프로파일의 고유 식별자                          |
| **Title**                | 프로파일의 제목                                       |
| **VC Schema ID**         | 연결된 VC Schema의 ID                                 |
| **Initiate Type**        | 발급 시작 방식 (`Issuer Initiate`, `User Initiate`)   |
| **Language**             | 프로파일 언어                                         |
| **Endpoints**            | 발급 요청을 수신할 Endpoint 목록                      |
| **E2E**                  | Cipher, Curve, Padding 설정                           |
| **Tags**                 | 태그 정보                                             |
| **ZKP Definition ID**    | ZKP 기반 발급에 사용될 Credential Definition ID       |

- `GO TO EDIT` 버튼 클릭 시 수정 화면으로 이동할 수 있습니다.

---

### ▸ Issue Profile 수정 (Update)

<img src="./images/3-3-3.issue-profile-edit.png" width="400"/>

- 등록된 Issue Profile의 내용을 수정할 수 있습니다.
- Initiate Type이 `Issuer Initiate`일 경우에만 `ZKP 발급 여부` 설정이 표시됩니다.

| 항목                     | 설명                                                  |
|--------------------------|-------------------------------------------------------|
| **VC Plan ID**           | 수정할 수 없습니다.                                   |
| **Title**                | 프로파일의 제목 수정 가능                             |
| **VC Schema ID**         | 이미 연동된 VC Schema는 수정 불가                     |
| **Initiate Type**        | 발급 시작 방식 수정 가능                              |
| **Language**             | 언어 설정 수정 가능                                   |
| **Endpoints**            | 추가 및 수정 가능                                     |
| **E2E**                  | Cipher, Curve, Padding 수정 가능                      |
| **Tags**                 | 태그 추가/삭제 가능                                   |
| **ZKP 발급 여부**        | 발급 방식이 Issuer Initiate일 때만 설정 가능           |
| **Credential Definition ID** | ZKP 발급 시 사용되는 Definition ID 지정             |

- 변경 사항 저장은 `UPDATE` 버튼을 통해 수행합니다.
- `RESET`은 입력값을 초기화, `BACK`은 상세 보기 화면으로 이동합니다.

---

## 3.4. ZKP Management

ZKP Management 메뉴는 **Zero-Knowledge Proof (ZKP)** 기반 Credential 발급을 위한 구성 요소들을 등록하고 관리하는 기능을 제공합니다.  
ZKP는 **이용자의 민감한 정보를 직접 공개하지 않고도 특정 정보를 증명할 수 있는 방식**으로, 보다 **프라이버시 친화적인 VC 발급**을 가능하게 합니다.

ZKP 기반 발급을 구성하기 위해서는 다음 세 가지 등록 절차가 반드시 선행되어야 하며,  
이 과정을 통해 생성된 정보는 이후 **Issue Profile 등록 시** ZKP 발급 항목에서 참조됩니다:

1. **ZKP Namespace 등록** – VC에 포함될 Claim(속성) 정의  
2. **Credential Schema 등록** – Namespace 기반으로 ZKP VC 구조 정의  
3. **Credential Definition 등록** – 실제 VC 발급에 사용될 스키마 기반 정의  
   → 이 정의는 ZKP 네트워크에서 사용될 고유한 Definition ID를 생성합니다.

ZKP Management는 위 절차를 단계별로 지원하며, 일반 VC Schema 관리와는 별도로 구성되어 있습니다.

---

### ▸ ZKP VC 발급 흐름 요약

1. **ZKP Namespace 등록**  
2. → **ZKP Credential Schema 등록 (Namespace 기반)**  
3. → **ZKP Credential Definition 등록 (Schema 기반)**  
4. → **Issue Profile 생성 시, ZKP 발급 옵션 활성화 및 Definition ID 연결**  
5. → **사용자 등록 후 ZKP 기반 VC 발급 수행**

> ZKP 기반 VC 발급을 위해서는 반드시 Issue Profile의 `Initiate Type`이 `Issuer Initiate`로 설정되어야 하며,  
> `ZKP 발급 여부`를 활성화하고 Definition ID를 선택해야 합니다.

> ZKP Management의 모든 메뉴는 **Issuer 등록 완료 후 사용 가능**하며,  
> 등록 순서를 준수하지 않을 경우 이후 Issue Profile 구성 및 VC 발급 시 오류가 발생할 수 있습니다.


## 3.4.1. ZKP Namespace Management

ZKP Namespace Management는 **Zero-Knowledge Proof (ZKP)** 기반 VC에 포함될 Claim(속성) 항목을 정의하는 공간입니다.  
여기서 등록하는 Namespace는 ZKP Credential Schema의 기초 단위로 사용되며, ZKP VC에 담길 **검증 가능한 데이터 항목들**을 명확하게 규정합니다.

Namespace는 **하나 이상의 Claim 항목(Label, Type, Caption 등)으로** 구성되며,  
각 Claim은 발급 시점에 사용자의 실제 정보와 대응되고, **ZKP 기반 증명**의 대상이 됩니다.

---

ZKP Namespace는 이후 **Credential Schema 등록 시 Claim 목록으로 참조**되며,  
Schema와 Definition을 거쳐 최종적으로 **Issue Profile의 ZKP Definition ID**로 연결됩니다.

따라서 ZKP 기반 VC 발급을 구성하려면, 반드시 이 메뉴를 통해 Claim 구조를 먼저 정의해야 하며,  
Namespace 등록 후 수정은 가능하지만 **삭제는 제한**됩니다.

> 예시 Claim 항목:
> - Label: birth_date  
> - Type: date  
> - Caption: 생년월일

---

> 본 메뉴는 **Issuer 등록 후 사용 가능**합니다. ZKP 기반 VC 발급을 위한 Claim 정의의 출발점으로,  
> 이후 Credential Schema 및 Definition 등록 시 필수로 참조됩니다.


### ▸ Namespace 목록

- 기존에 등록된 ZKP Namespace 리스트를 확인할 수 있습니다.
- 각 항목에는 ID, Name, Schema Count, 등록/수정 일시가 표시됩니다.
- Name을 클릭하면 상세 화면으로 이동합니다.

<img src="./images/3-4-1.namespace-management.png" width="700"/>

| 항목            | 설명                                     |
|-----------------|------------------------------------------|
| **ID**          | Namespace의 고유 ID                      |
| **Name**        | Namespace 이름 (클릭 시 상세 화면 이동)  |
| **Schema Count**| 해당 Namespace를 참조 중인 Schema 수     |
| **Registered At**| 최초 등록 일시                          |
| **Updated At**   | 마지막 수정 일시                         |

- `REGISTER` 버튼: 새로운 Namespace 등록 화면으로 이동  
- 항목 선택 시 `UPDATE`, `DELETE` 버튼 활성화

---

### ▸ Namespace 등록

<img src="./images/3-4-1.namespace-registration.png" width="700"/>

| 항목             | 설명                                                              |
|------------------|-------------------------------------------------------------------|
| **Namespace ID** | 고유 식별자 (중복 체크 필요)                                       |
| **Name**         | Namespace 이름                                                    |
| **Ref**          | 관련 설명이나 참조 링크                                           |
| **Items**        | 포함할 Claim 항목 목록 (Label, Type, Caption 필수)                 |

- `ADD ITEM` 버튼으로 항목을 추가할 수 있습니다.  
- 등록 완료 시 `REGISTER` 버튼 클릭  
- `RESET` 버튼은 폼 초기화, `CANCEL`은 등록 취소

---

### ▸ Namespace 상세 정보

<img src="./images/3-4-1.namespace-detail.png" width="700"/>

- 등록된 Namespace 정보를 열람할 수 있습니다.
- 하단 Attributes 테이블에서 Claim 항목 목록을 확인할 수 있습니다.

---

### ▸ Namespace 수정

> 네임스페이스를 사용하는 Credential Schema가 없을 경우 수정할 수 있습니다.
> VC Schema에서 해당 네임스페이스를 참고하고 있을 경우 수정할 수 없습니다.

<img src="./images/3-4-1.namespace-edit.png" width="700"/>

- Claim 항목은 추가/삭제가 가능하며, 내용 수정도 가능합니다.
- 수정 후 `UPDATE` 버튼으로 저장

---

## 3.4.2. Credential Schema Management

Credential Schema Management는 ZKP 기반 Credential 발급 시 사용될 **ZKP Credential Schema**를 정의하고 관리하는 메뉴입니다.  

ZKP Credential Schema는 **ZKP Namespace에 정의된 Claim 항목들을 기반으로 구성**되며, VC에 어떤 정보를 포함할 것인지 결정합니다.

이 Schema는 일반 VC에서 사용하는 VC Schema와는 별도의 구조이며, **ZKP 방식에 최적화된 Claim 정보만을 포함**합니다.  
Schema에 포함된 각 Claim은 이후 ZKP 증명의 입력 값으로 활용되며, **ZKP Definition과 1:1로 연결**됩니다.

---

### 일반 VC Schema와 ZKP Credential Schema의 차이점

| 항목 | VC Schema (일반 VC) | ZKP Credential Schema |
|------|----------------------|------------------------|
| 참조 대상 | 일반 VC Namespace | ZKP Namespace |
| 사용 목적 | 일반 VC 발급용 Claim 구조 | ZKP 증명용 VC 구조 |
| 활용 방식 | Issue Profile에서 참조 | Credential Definition으로 연결 |
| 주요 특징 | 다양한 Claim 포함 가능 | **ZKP 증명 가능한 Claim으로만 구성** |

---

Credential Schema는 이후 **Credential Definition 등록 시 필수로 선택**되며,  
Definition ID를 통해 최종적으로 Issue Profile에 연결됩니다.

Credential Schema를 구성할 때는 **해당 Claim 항목들이 ZKP 증명에 적합한 구조인지**를 검토한 뒤,  
ZKP Namespace를 기반으로 Claim들을 조합하여 구성해야 합니다.

> 본 메뉴는 **Issuer 등록 및 ZKP Namespace 등록 후 사용 가능합니다.**  
> Credential Schema는 ZKP 발급 구조를 정의하며, 이후 ZKP Definition 생성 및 VC 발급의 핵심 기준이 됩니다.

### ▸ Credential Schema 목록

<img src="./images/3-4-2.credential-schema-management.png" width="700"/>

- 등록된 스키마가 없다면 “No rows”로 표시됩니다.
- `REGISTER` 버튼 클릭 시 새 스키마 등록 가능

---

### ▸ Credential Schema 등록

<img src="./images/3-4-2.credential-schema-registration.png" width="700"/>

| 항목         | 설명                                                           |
|--------------|----------------------------------------------------------------|
| **Name**     | 스키마 이름                                                    |
| **Version**  | 스키마 버전 (예: 1.0)                                           |
| **Tag**      | 스키마 식별용 태그                                              |
| **Attributes**| 구성 항목 목록 (Namespace ID, Caption, Label, Type으로 구성됨) |

- `ADD ATTRIBUTE` 버튼으로 구성 항목 추가  
- 항목은 ZKP Namespace를 기반으로 선택

---

### ▸ Credential Schema 상세 정보

<img src="./images/3-4-2.credential-schema-detail.png" width="700"/>

- 등록된 스키마의 상세 정보를 확인할 수 있습니다.
- 각 항목(Label, Type, Caption)이 테이블 형태로 표시됩니다.

---

## 3.4.3. Credential Definition Management

Credential Definition Management는 ZKP 기반 Verifiable Credential(VC) 발급을 위한  
**Credential Definition**을 정의하고 관리하는 메뉴입니다.

Credential Definition은 특정 ZKP Credential Schema를 기반으로,  
어떤 방식으로 ZKP Credential을 생성하고 증명할지를 지정하는 **발급 정책의 정의서**입니다.  
이 Definition은 **VC 발급 시 ZKP Credential을 함께 생성하기 위한 기준 정보**로 사용됩니다.

---

### VC 발급 시 ZKP Credential 생성 및 사용자 전달

ZKP 발급을 위해 구성된 **Issue Profile**이 이 Definition을 참조하고,  
해당 프로파일을 기반으로 사용자가 VC를 발급받을 경우, 시스템은 다음 두 가지 Credential을 생성합니다:

1. 일반 Verifiable Credential (JSON-LD 또는 JWT 형태)  
2. ZKP Credential (정형화된 ZKP 구조, 예: CL 증명용 데이터)

이때 생성된 ZKP Credential은 내부적으로 Definition에서 정의한 구조 및 정책을 따르며,  
**발급 직후 사용자에게 함께 전달됩니다.**  
사용자는 이를 향후 검증자에게 제출하여 **Zero-Knowledge Proof를 통한 증명**을 수행할 수 있습니다.

Definition 자체는 블록체인에 발행되 조회할 수 있으며, 
**시스템 내부에서 증명 가능한 ZKP Credential 생성을 위한 참조 정보**와 검증을 위한 용도로 사용됩니다.

---

### Credential Definition 구성 요소

- **Credential Schema**: 정의가 참조하는 ZKP Credential Schema
- **Definition Alias**: Definition을 구분하기 위한 별칭
- **Definition Version**: Definition의 버전 정보
- **Definition Type**: 증명 방식 (예: `CL`)
- **Definition Tag**: 관리 및 검색을 위한 태그

---

> 본 메뉴는 **Issuer 등록 및 ZKP Credential Schema 등록 후 사용 가능합니다.**  
> Credential Definition은 VC 발급 시, **ZKP Credential을 함께 생성하여 사용자에게 전달하기 위한 참조 정보**입니다. 

### ▸ Credential Definition 목록

<img src="./images/3-4-3.credential-definition-management.png" width="700"/>

- 등록된 Definition이 없는 경우 “No rows”로 표시됩니다.
- `REGISTER` 버튼 클릭 시 새 Definition 등록 가능

---

### ▸ Credential Definition 등록

<img src="./images/3-4-3.credential-definition-registration.png" width="500"/>

| 항목               | 설명                                               |
|--------------------|----------------------------------------------------|
| **Credential Schema** | 등록된 스키마 선택 (드롭다운 방식)               |
| **Definition Alias** | 정의의 식별용 별칭                                 |
| **Definition Version** | 정의 버전                                        |
| **Definition Type** | 사용 방식 선택 (예: CL)                            |
| **Definition Tag** | 태그 정보                                           |

- 등록 완료 후 `REGISTER` 버튼 클릭
- `Check Availability`로 Definition Alias 중복 확인 가능

---

### ▸ Credential Definition 상세 정보

<img src="./images/3-4-3.credential-definition-detail.png" width="500"/>

- Definition ID, Alias, 연결된 Schema, 상태 등의 정보 확인 가능
- 정의된 내용은 삭제 불가하며, 열람만 가능

---

## 3.5. User Management

User Management는 Verifiable Credential(VC) 발급 시 참조되는 **사용자 정보를 확인**할 수 있는 메뉴입니다.  
사용자 정보는 VC 발급에 필요한 **DID, PII, VC 데이터(Claim)** 등을 포함하며,  
해당 정보는 발급 시점에 시스템에 자동 등록됩니다.

등록된 사용자는 VC 발급 대상자로 간주되며, 시스템은 이 정보를 바탕으로 **사용자에게 맞춤화된 VC를 생성**합니다.  
사용자 정보는 VC Schema와 연결되어 있으며, VC 생성 시 필드 값 추출 및 검증에 사용됩니다.

---

### ▸ 사용자 목록

<img src="./images/3-5-1.user-management.png" width="700"/>

| 항목 | 설명 |
|------|------|
| **DID** | 사용자의 DID. 클릭 시 상세 정보 화면으로 이동합니다. |
| **VC Schema** | 사용자가 연결된 VC Schema의 ID |
| **Registered At** | 최초 등록 일시 |
| **Updated At** | 마지막 수정 일시 |


---


### ▸ 사용자 상세 정보

<img src="./images/3-5-3.user-detail.png" width="500"/>

| 항목 | 설명 |
|------|------|
| **DID** | 사용자의 고유 식별자 (Decentralized Identifier) |
| **VC Schema Name** | 해당 사용자의 VC가 참조하는 VC Schema ID |
| **PII** | 사용자의 개인 식별 정보(Personal Identifiable Info) |
| **User VC Info** | 사용자에게 발급할 VC 데이터 (JSON 형태) |
| **Created At** | 사용자 등록 일시 |
| **Updated At** | 마지막 수정 일시 |

---

## 3.6. Issued VC Management

Issued VC Management는 발급된 Verifiable Credential(VC)의 목록을 조회하고 상태를 관리할 수 있는 기능입니다.  
해당 화면에서는 VC 상태를 변경하는 **UPDATE 기능**이 제공됩니다.

### ▸ Issued VC 목록

<img src="./images/3-6-1.issued-vc-management.png" width="700"/>

| 항목 | 설명 |
|------|------|
| **VC ID** | VC 고유 식별자 (상세로 이동) |
| **DID** | 발급 대상 사용자 DID |
| **Registered At** | 발급 일시 |

- 목록에서 항목을 선택하면 상단의 `UPDATE` 버튼이 활성화됩니다.

### ▸ Issued VC Status 변경

선택한 VC의 상태를 변경할 수 있습니다. 상태 변경은 아래 조건에 따라 가능합니다.

<img src="./images/3-6-2.issued-vc-update.png" width="700"/>

| 현재 상태 | 변경 가능 상태 |
|-----------|----------------|
| `ACTIVE` | `INACTIVE`, `REVOKED` |
| `INACTIVE` | `ACTIVE`, `REVOKED` |
| `REVOKED` | (변경 불가) |

> **참고**  
> `REVOKED` 상태는 불변이며, 복구 또는 상태 변경이 불가능합니다.  
> 상태 변경은 사용자 VC의 유효성 관리와 추적을 위한 기능입니다.

1. VC 목록에서 항목 선택 후 `UPDATE` 버튼 클릭  
2. **Change VC Status** 다이얼로그에서 상태 선택  
3. 확인 후 저장하면 VC 상태가 변경됩니다

### ▸ Issued VC 상세 정보

<img src="./images/3-6-3.issued-vc-detail.png" width="500"/>

| 항목 | 설명 |
|------|------|
| **VC ID** | 고유 식별자 |
| **DID** | 사용자 DID |
| **VC Schema** | 발급한 VC 스키마 |
| **Status** | VC 상태 (`ACTIVE`, `REVOKE` 등) |
| **Created At / Updated At** | 생성 및 수정 시각 |

 
## 3.7. Admin Management

`Admin Management` 메뉴는 Issuer Admin Console에 접근할 수 있는 관리자 계정을 관리하는 기능입니다.  

Issuer 서버를 설치하면 기본적으로 `admin@opendid.omnione.net` 계정이 ROOT 권한으로 자동 생성됩니다.  
이 계정은 시스템 내 유일한 ROOT 계정이며, 삭제할 수 없습니다.

관리자 계정은 **ROOT**와 **Normal Admin** 두 가지 권한 유형으로 구분됩니다.  
ROOT 계정은 `Admin Management` 메뉴에서 모든 기능을 수행할 수 있으며, Normal Admin은 일반적인 조회 기능만 가능합니다.

---
> **참고:** 현재는 ROOT 계정과 Normal Admin 계정 간의 권한 차이는  
> `Admin Management` 메뉴에서 표시되는 버튼의 차이(Root만 REGISTER / DELETE / CHANGE PASSWORD 가능) 외에는 없습니다.  
> 그 외 시스템의 다른 메뉴에 대한 접근 권한이나 기능 제한은 아직 적용되어 있지 않습니다.
---

<br/>

### 3.7.1 Admin 목록 조회


`Admin Management` 메뉴에 진입하면 등록된 관리자 계정들의 목록이 테이블 형태로 표시됩니다.

<img src="./images/3-7-1.admin-management.png" width="800"/>


| 번호 | 항목                    | 설명                                                             |
| ---- | ----------------------- | ---------------------------------------------------------------- |
| 1    | **REGISTER 버튼**       | 새로운 관리자 계정을 등록할 수 있는 등록 페이지로 이동합니다.       |
| 2    | **DELETE 버튼**         | 선택한 관리자 계정을 삭제합니다. (ROOT 관리자만 가능)              |
| 3    | **CHANGE PASSWORD 버튼** | 선택한 관리자 계정의 비밀번호를 변경할 수 있습니다.                |
| 4    | **ID**                  | 등록된 관리자 계정의 이메일 ID입니다.                              |
| 5    | **Role**                | 해당 관리자 계정의 역할(Role)입니다. (예: ROOT, Normal Admin 등)   |
| 6    | **Registered At**       | 해당 계정이 최초 등록된 일시입니다.                               |
| 7    | **Updated At**          | 마지막으로 수정된 일시입니다.     

<br/>

### 3.7.2. Admin 등록

`Admin Management` 화면에서 **REGISTER** 버튼을 클릭하면, 아래와 같은 등록 화면으로 이동합니다.

<img src="./images/3-7-2.admin-registration.png" width="500"/>

| 번호 | 항목                        | 설명                                                                |
| ---- | --------------------------- | ------------------------------------------------------------------- |
| 1    | **ID**                      | 등록할 관리자 계정의 ID입니다. 이메일 형식을 사용해야 합니다.         |
| 2    | **Check Availability 버튼** | 입력한 ID가 중복되지 않는지 확인합니다.                             |
| 3    | **Role**                    | 등록할 관리자 계정의 권한을 선택합니다. (예: Normal Admin)           |
| 4    | **Password**                | 로그인 시 사용할 비밀번호를 입력합니다.                              |
| 5    | **Re-enter Password**       | 비밀번호를 한 번 더 입력하여 일치 여부를 확인합니다.                |
| 6    | **REGISTER 버튼**           | 입력한 정보를 바탕으로 관리자 계정을 등록합니다.                    |
| 7    | **RESET 버튼**              | 모든 입력값을 초기화합니다.                                         |
| 8    | **CANCEL 버튼**             | 등록을 취소하고 이전 화면으로 돌아갑니다.                           |


[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/unrelease-V2.0.0.0/OepnDID_Installation_Guide-V2.0.0.0_ko.md
