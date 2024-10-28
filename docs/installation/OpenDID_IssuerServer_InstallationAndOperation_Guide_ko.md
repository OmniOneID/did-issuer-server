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

Open DID Issuer Server Installation And Operation Guide
==

- Date: 2024-09-02
- Version: v1.0.0

목차
==
- [1. 소개](#1-소개)
  - [1.1. 개요](#11-개요)
  - [1.2. Issuer 서버 정의](#12-issuer-서버-정의)
  - [1.3. 시스템 요구 사항](#13-시스템-요구-사항)
- [2. 사전 준비 사항](#2-사전-준비-사항)
  - [2.1. Git 설치](#21-git-설치)
  - [2.2. PostgreSQL 설치](#22-postgresql-설치)
- [3. GitHub에서 소스 코드 복제하기](#3-github에서-소스-코드-복제하기)
  - [3.1. 소스코드 복제](#31-소스코드-복제)
  - [3.2. 디렉토리 구조](#32-디렉토리-구조)
- [4. 서버 구동 방법](#4-서버-구동-방법)
  - [4.1. IntelliJ IDEA로 구동하기 (Gradle 지원)](#41-intellij-idea로-구동하기-gradle-지원)
    - [4.1.1. IntelliJ IDEA 설치 및 설정](#411-intellij-idea-설치-및-설정)
    - [4.1.2. IntelliJ에서 프로젝트 열기](#412-intellij에서-프로젝트-열기)
    - [4.1.3. Gradle 빌드](#413-gradle-빌드)
    - [4.1.4. 서버 구동](#414-서버-구동)
    - [4.1.5. 데이터베이스 설치](#415-데이터베이스-설치)
    - [4.1.6. 서버 설정](#416-서버-설정)
  - [4.2. 콘솔 명령어로 구동하기](#42-콘솔-명령어로-구동하기)
    - [4.2.1. Gradle 빌드 명령어](#421-gradle-빌드-명령어)
    - [4.2.2. 서버 구동 방법](#422-서버-구동-방법)
    - [4.2.3. 데이터베이스 설치](#423-데이터베이스-설치)
    - [4.2.4. 서버 설정 방법](#424-서버-설정-방법)
  - [4.3. Docker로 구동하기](#43-docker로-구동하기)
- [5. 설정 가이드](#5-설정-가이드)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring 기본 설정](#511-spring-기본-설정)
    - [5.1.2. 서버 설정](#512-서버-설정)
    - [5.1.3. TAS 설정](#513-tas-설정)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. 로깅 설정](#521-로깅-설정)
  - [5.3. database.yml](#53-databaseyml)
    - [5.3.1. Spring Liquibase 설정](#531-spring-liquibase-설정)
    - [5.3.2. 데이터소스 설정](#532-데이터소스-설정)
    - [5.3.3. JPA 설정](#533-jpa-설정)
  - [5.4. wallet.yml](#54-walletyml)
    - [5.4.1. 월렛 설정](#541-월렛-설정)
  - [5.5. issue.yml](#55-issueyml)
    - [5.5.1. Issuer 설정](#551-issuer-설정)
  - [5.6. blockchain.properties](#56-blockchainproperties)
    - [5.6.1. 블록체인 연동 설정](#561-블록체인-연동-설정)
- [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용)
  - [6.1. 프로파일 개요 (`sample`, `dev`)](#61-프로파일-개요-sample-dev)
    - [6.1.1. `sample` 프로파일](#611-sample-프로파일)
    - [6.1.2. `dev` 프로파일](#612-dev-프로파일)
  - [6.2. 프로파일 설정 방법](#62-프로파일-설정-방법)
    - [6.2.1. IDE를 사용한 서버 구동 시](#621-ide를-사용한-서버-구동-시)
    - [6.2.2. 콘솔 명령어를 사용한 서버 구동 시](#622-콘솔-명령어를-사용한-서버-구동-시)
    - [6.2.3. Docker를 사용한 서버 구동 시](#623-docker를-사용한-서버-구동-시)
- [7. Docker로 빌드 후 구동하기](#7-docker로-빌드-후-구동하기)
  - [7.1. Docker 이미지 빌드 방법 (`Dockerfile` 기반)](#71-docker-이미지-빌드-방법-dockerfile-기반)
  - [7.2. Docker 이미지 실행](#72-docker-이미지-실행)
  - [7.3. Docker Compose를 이용한 구동](#73-docker-compose를-이용한-구동)
    - [7.3.1. `docker-compose.yml` 파일 설명](#731-docker-composeyml-파일-설명)
    - [7.3.2. 컨테이너 실행 및 관리](#732-컨테이너-실행-및-관리)
    - [7.3.3. 서버 설정 방법](#733-서버-설정-방법)
- [8. Docker PostgreSQL 설치하기](#8-docker-postgresql-설치하기)
  - [8.1. Docker Compose를 이용한 PostgreSQL 설치](#81-docker-compose를-이용한-postgresql-설치)
  - [8.2. PostgreSQL 컨테이너 실행](#82-postgresql-컨테이너-실행)
    

# 1. 소개

## 1.1. 개요
본 문서는 Issuer 서버의 설치 및 구동에 관한 가이드를 제공합니다. 설치 과정, 설정 방법, 그리고 구동 절차를 단계별로 설명하여, 사용자가 이를 효율적으로 설치하고 운영할 수 있도록 안내합니다.

OpenDID의 전체 설치에 대한 가이드는 [Open DID Installation Guide]를 참고해 주세요.

<br/>

## 1.2. Issuer 서버 정의

Issuer 서버는 Open DID 내에서 VC 발급, VC 폐기, 상태 변경 등 다양한 기능을 제공합니다.

<br/>

## 1.3. 시스템 요구 사항
- **Java 17** 이상
- **Gradle 7.0** 이상
- **Docker** 및 **Docker Compose** (Docker 사용 시)
- 최소 **2GB RAM** 및 **10GB 디스크 공간**

<br/>

# 2. 사전 준비 사항

이 장에서는 Open DID 프로젝트의 구성요소를 설치하기 전, 사전에 필요한 준비 항목들을 안내합니다.

## 2.1. Git 설치

`Git`은 분산 버전 관리 시스템으로, 소스 코드의 변경 사항을 추적하고 여러 개발자 간의 협업을 지원합니다. Git은 Open DID 프로젝트의 소스 코드를 관리하고 버전 관리를 위해 필수적입니다. 

설치가 성공하면 다음 명령어를 사용하여 Git의 버전을 확인할 수 있습니다.

```bash
git --version
```

> **참고 링크**
> - [Git 설치 가이드](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL 설치
Issuer 서버를 구동하려면 데이터베이스 설치가 필요하며, Open DID에서는 PostgreSQL을 사용합니다.

> **참고 링크**
- [PostgreSQL 설치 가이드 문서](https://www.postgresql.org/download/)
- [8. Docker postgreSQL 설치하기](#8-docker-postgresql-설치하기)

<br/>


# 3. GitHub에서 소스 코드 복제하기

## 3.1. 소스코드 복제

`git clone` 명령은 GitHub에 호스팅된 원격 저장소에서 로컬 컴퓨터로 소스 코드를 복제하는 명령어입니다. 이 명령을 사용하면 프로젝트의 전체 소스 코드와 관련 파일들을 로컬에서 작업할 수 있게 됩니다. 복제한 후에는 저장소 내에서 필요한 작업을 진행할 수 있으며, 변경 사항은 다시 원격 저장소에 푸시할 수 있습니다.

터미널을 열고 다음 명령어를 실행하여 Issuer 서버의 리포지토리를 로컬 컴퓨터에 복사합니다.
```bash
# Git 저장소에서 리포지토리 복제
git clone https://github.com/OmniOneID/did-issuer-server.git

# 복제한 리포지토리로 이동
cd did-issuer-server
```

> **참고 링크**
> - [Git Clone 가이드](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 3.2. 디렉토리 구조
복제된 프로젝트의 주요 디렉토리 구조는 다음과 같습니다:

```
did-issuer-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE-dependencies.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   ├── api
│       └── Issuer_API.md
│   ├── errorCode
│       └── Issuer_ErrorCode.md
│   ├── installation
│       └── OpenDID_IssuerServer_InstallationAndOperation_Guide.md
│   └── db
│       └── OpenDID_TableDefinition_Issuer.md
└── source
    └── issuer
        ├── gradle
        ├── libs
            └── did-sdk-common-1.0.0.jar
            └── did-blockchain-sdk-server-1.0.0.jar
            └── did-core-sdk-server-1.0.0..jar
            └── did-crypto-sdk-server-1.0.0.jar
            └── did-datamodel-sdk-server-1.0.0.jar
            └── did-wallet-sdk-server-1.0.0.jar
        ├── sample
        └── src
        └── build.gradle
        └── README.md
```

| Name                    | Description                              |
| ----------------------- | ---------------------------------------- |
| CHANGELOG.md            | 프로젝트의 버전별 변경 사항              |
| CODE_OF_CONDUCT.md      | 기여자들을 위한 행동 강령                |
| CONTRIBUTING.md         | 기여 지침 및 절차                        |
| LICENSE                 | 라이선스 |
| LICENSE-dependencies.md | 프로젝트 의존 라이브러리의 라이선스 정보 |
| MAINTAINERS.md          | 프로젝트 관리자를 위한 지침              |
| RELEASE-PROCESS.md      | 새로운 버전을 릴리스하는 절차            |
| SECURITY.md             | 보안 정책 및 취약성 보고 방법            |
| docs                    | 문서                                     |
| ┖ api                   | API 가이드 문서                          |
| ┖ errorCode             | 오류 코드 및 문제 해결 가이드            |
| ┖ installation          | 설치 및 설정 가이드                      |
| ┖ db                    | 데이터베이스 ERD, 테이블 명세서          |
| source                  | 소스 코드                                |
| ┖ did-issuer-server     | Issuer 서버 소스 코드 및 빌드 파일          |
| ┖ gradle                | Gradle 빌드 설정 및 스크립트             |
| ┖ libs                  | 외부 라이브러리 및 의존성                |
| ┖ sample                | 샘플 파일                                |
| ┖ src                   | 주요 소스 코드 디렉토리                  |
| ┖ build.gradle          | Gradle 빌드 설정 파일                    |
| ┖ README.md             | 소스 코드 개요 및 안내                   |

<br/>


# 4. 서버 구동 방법
이 장에서는 서버를 구동하는 세 가지 방법을 안내합니다.

프로젝트 소스는 `source` 디렉토리 하위에 위치하며, 각 구동 방법에 따라 해당 디렉토리에서 소스를 불러와 설정해야 합니다.

1. **IDE를 사용하는 방법**: 통합 개발 환경(IDE)에서 프로젝트를 열고, 실행 구성을 설정한 후 서버를 직접 실행할 수 있습니다. 이 방법은 개발 중에 코드 변경 사항을 빠르게 테스트할 때 유용합니다.

2. **Build 후 콘솔 명령어를 사용하는 방법**: 프로젝트를 빌드한 후, 생성된 JAR 파일을 콘솔에서 명령어(`java -jar`)로 실행하여 서버를 구동할 수 있습니다. 이 방법은 서버를 배포하거나 운영 환경에서 실행할 때 주로 사용됩니다.

3. **Docker로 빌드하는 방법**: 서버를 Docker 이미지로 빌드하고, Docker 컨테이너로 실행할 수 있습니다. 이 방법은 환경 간 일관성을 유지하며, 배포 및 스케일링이 용이한 장점이 있습니다.
   
## 4.1. IntelliJ IDEA로 구동하기 (Gradle 지원)

IntelliJ IDEA는 Java 개발에 널리 사용되는 통합 개발 환경(IDE)으로, Gradle과 같은 빌드 도구를 지원하여 프로젝트 설정 및 의존성 관리가 매우 용이합니다. Open DID의 서버는 Gradle을 사용하여 빌드되므로, IntelliJ IDEA에서 쉽게 프로젝트를 설정하고 서버를 실행할 수 있습니다.

### 4.1.1. IntelliJ IDEA 설치 및 설정
1. IntelliJ를 설치합니다. (설치 방법은 아래 링크를 참조)

> **참고 링크**
> - [IntelliJ IDEA 다운로드](https://www.jetbrains.com/idea/download/)

### 4.1.2. IntelliJ에서 프로젝트 열기
- IntelliJ를 실행시키고 `File -> New -> Project from Existing Sources`를 선택합니다. 파일 선택 창이 나타나면 [3.1. 소스코드 복제](#31-소스코드-복제) 에서 복제한 리포지토리에서 'source/did-issuer-server' 폴더를 선택합니다.
- 프로젝트를 열면 build.gradle 파일이 자동으로 인식됩니다.
- Gradle이 자동으로 필요한 의존성 파일들을 다운로드하며, 이 과정이 완료될 때까지 기다립니다.

### 4.1.3. Gradle 빌드
- IntelliJ IDEA의 `Gradle` 탭에서 `Tasks -> build -> build`를 실행합니다. 
- 빌드가 성공적으로 완료되면, 프로젝트가 실행 가능한 상태로 준비됩니다.

### 4.1.4. 서버 구동
- IntelliJ IDEA의 Gradle 탭에서 Tasks -> application -> bootRun을 선택하고 실행합니다.
- Gradle이 자동으로 서버를 빌드하고 실행합니다.
- 콘솔 로그에서 "Started [ApplicationName] in [time] seconds" 메시지를 확인하여 서버가 정상적으로 실행되었는지 확인합니다.

> **주의**
> - Issuer 서버는 초기에 sample 프로파일로 설정되어 있습니다.
> - sample 프로파일로 설정시, 필수 설정(예: 데이터베이스)을 무시하고 서버가 구동됩니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.


### 4.1.5. 데이터베이스 설치
Issuer 서버는 운영에 필요한 데이터를 데이터베이스에 저장하므로, 서버를 운영하려면 반드시 데이터베이스가 설치되어야 합니다. Open DID의 서버는 PostgreSQL 데이터베이스를 사용합니다. PostgreSQL 서버의 설치 방법은 여러가지가 있지만, Docker를 이용한 설치가 가장 간편하고 쉽습니다. PostgreSQL의 설치 방법은 [2.2. PostgreSQL 설치](#22-postgresql-설치) 장을 참고해 주세요.

<br/>

### 4.1.6. 서버 설정
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, 이메일 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `src/main/resource/config` 경로에 위치해 있습니다.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드) 를 참고해 주세요.

<br/>

## 4.2. 콘솔 명령어로 구동하기

콘솔 명령어를 사용하여 Open DID 서버를 구동하는 방법을 안내합니다. Gradle을 이용해 프로젝트를 빌드하고, 생성된 JAR 파일을 사용하여 서버를 구동하는 과정을 설명합니다.

### 4.2.1. Gradle 빌드 명령어

- gradlew를 사용하여 소스를 빌드합니다.
  ```shell
    # 복제한 리포지토리로의 소스폴더로 이동
    cd source/did-issuer-server

    # Gradle Wrapper 실행 권한을 부여
    chmod 755 ./gradlew

    # 프로젝트를 클린 빌드 (이전 빌드 파일을 삭제하고 새로 빌드)
    ./gradlew clean build
  ```
  > 참고
  > - gradlew은 Gradle Wrapper의 줄임말로, 프로젝트에서 Gradle을 실행하는 데 사용되는 스크립트입니다. 로컬에 Gradle이 설치되어 있지 않더라도, 프로젝트에서 지정한 버전의 Gradle을 자동으로 다운로드하고 실행할 수 있도록 해줍니다. 따라서 개발자는 Gradle 설치 여부와 상관없이 동일한 환경에서 프로젝트를 빌드할 수 있게 됩니다.

- 빌드된 폴더로 이동하여 JAR 파일이 생성된 것을 확인합니다.
    ```shell
      cd build/libs
      ls
    ```
- 이 명령어는 `did-issuer-server-1.0.0.jar` 파일을 생성합니다.

<br/>

### 4.2.2. 서버 구동 방법
빌드된 JAR 파일을 사용하여 서버를 구동합니다:

```bash
java -jar did-issuer-server-1.0.0.jar
```

> **주의**
> - Issuer 서버는 초기에 sample 프로파일로 설정되어 있습니다.
> - sample 프로파일로 설정시, 필수 설정(예: 데이터베이스)을 무시하고 서버가 구동됩니다. 자세한 내용은 [6. 프로파일 설정 및 사용](#6-프로파일-설정-및-사용) 장을 참고해 주세요.

<br/>

### 4.2.3. 데이터베이스 설치
Issuer 서버는 운영에 필요한 데이터를 데이터베이스에 저장하므로, 서버를 운영하려면 반드시 데이터베이스가 설치되어야 합니다. Open DID의 서버는 PostgreSQL 데이터베이스를 사용합니다. PostgreSQL 서버의 설치 방법은 여러가지가 있지만, Docker를 이용한 설치가 가장 간편하고 쉽습니다. PostgreSQL의 설치 방법은 [2.2. PostgreSQL 설치](#22-postgresql-설치) 장을 참고해 주세요.

<br/>

### 4.2.4. 서버 설정 방법
- 서버는 배포 환경에 맞게 필요한 설정을 수정해야 하며, 이를 통해 서버가 안정적으로 작동할 수 있도록 해야 합니다. 예를 들어, 데이터베이스 연결 정보, 포트 번호, 이메일 연동 정보 등 각 환경에 맞는 구성 요소들을 조정해야 합니다.
- 서버의 설정 파일은 `src/main/resource/config` 경로에 위치해 있습니다.
- 자세한 설정 방법은 [5. 설정 가이드](#5-설정-가이드) 를 참고하십시오.

<br/>

## 4.3. Docker로 구동하기
- Docker 이미지 빌드, 설정, 실행 등의 과정은 아래 [7. Docker로 빌드 후 구동하기](#7-docker로-빌드-후-구동하기) 를 참고하세요.

<br/>


# 5. 설정 가이드
이 장에서는 서버의 모든 설정 파일에 포함된 각 설정 값에 대해 안내합니다. 각 설정은 서버의 동작과 환경을 제어하는 중요한 요소로, 서버를 안정적으로 운영하기 위해 적절한 설정이 필요합니다. 항목별 설명과 예시를 참고하여 각 환경에 맞는 설정을 적용하세요.

🔒 아이콘이 있는 설정은 기본적으로 고정된 값이거나, 일반적으로 수정할 필요가 없는 값임을 참고해주세요.

## 5.1. application.yml

### 5.1.1. Spring 기본 설정
* `spring.application.name`: 
    - 애플리케이션의 이름을 지정합니다.
    - 용도: 다른 서비스에서 이 애플리케이션을 식별하는 데 사용됩니다.
    - 예시: `Issuer`

* `spring.profiles`:  
    - 활성화할 프로필과 프로필 그룹을 정의합니다.
    - 용도: 
        - `active`: 기본적으로 활성화될 프로필을 지정합니다.
        - `group`: 환경별(dev, sample)로 활성화할 프로필 그룹을 정의합니다.
    - 설정값 예시 및 설명:
      ```yaml
      profiles:
        active: dev
        group:
          dev:            
            - databases   # 데이터베이스 관련 설정 (application-database.yml)
            - wallet      # 월렛 관련 설정 (application-wallet.yml)
            - logging     # 로깅 설정 (application-logging.yml)
            - spring-docs # Swagger API 문서화 설정 (application-spring-docs.yml, 선택적)
            - issue       # Issuer 서버 설정 (application-issue.yml)            
      ```

* `spring.jackson`: 🔒 
    - JSON 직렬화/역직렬화 관련 설정입니다. 다른 서버와 통신간 공통으로 사용되는 값입니다.
    - 예시:
      ```yaml
      default-property-inclusion: non_null
      serialization:
        fail-on-empty-beans: false
      ```

### 5.1.2. 서버 설정 
* `server.port`:  
    - 애플리케이션이 실행될 포트 번호입니다. Issuer 서버의 포트 설정의 기본값은 8091 입니다.
    - 값 : 8091

### 5.1.3. TAS 설정 
Issuer 서비스는 TAS 서버와 통신을 합니다. 직접 구축한 TAS서버의 주소값을 설정하면 됩니다.
* `tas.url`:  
    - TAS(Trust Anchor Service) 서비스의 URL입니다. 인증이나 신뢰 검증에 사용될 수 있습니다.
    - 예시: `http://localhost:8090/contextpath/tas`

## 5.2. application-logging.yml

### 5.2.1. 로깅 설정
* `logging.level`: 
    - 로그 레벨을 설정합니다.
    - 레벨을 debug 설정함으로써, 지정된 패키지에 대해 DEBUG 레벨 이상(INFO, WARN, ERROR, FATAL)의 모든 로그 메시지를 볼 수 있습니다.

전체 예시:
```yaml
logging:
  level:
    org.omnione: debug
```

## 5.3. database.yml

### 5.3.1. Spring Liquibase 설정 
* `spring.liquibase.change-log`: 🔒 
    - 데이터베이스 변경 로그 파일의 위치를 지정합니다. Liquibase가 데이터베이스 스키마 변경을 추적하고 적용하는 데 사용하는 로그 파일의 위치입니다.
    - 예시: `classpath:/db/changelog/master.xml`

* `spring.liquibase.enabled`: 🔒 
    - Liquibase 활성화 여부를 설정합니다. true로 설정 시 애플리케이션 시작 시 Liquibase가 실행되어 데이터베이스 마이그레이션을 수행합니다.
    - 예시: `true` [dev], `false` [sample]

* `spring.liquibase.fall-on-error`: 🔒 
    - Liquibase가 데이터베이스 마이그레이션을 수행하는 동안 오류가 발생했을 때의 동작을 제어합니다. sample에서만 설정합니다.
    - 예시: `false` [sample]

### 5.3.2. 데이터소스 설정
* `spring.datasource.driver-class-name`:  
    - 사용할 데이터베이스 드라이버 클래스를 지정합니다. 데이터베이스에 연결하기 위한 JDBC 드라이버를 지정합니다. 현재는 postgre를 기준으로 작성됐습니다.
    - 예시: `org.postgresql.Driver`

* `spring.datasource.url`:  
    - 데이터베이스 연결 URL입니다. 애플리케이션이 연결할 데이터베이스의 위치와 이름을 지정합니다. postgre를 기준으로 작성되었습니다.
    - 예시: `jdbc:postgresql://localhost:5432/issuer_db`

* `spring.datasource.username`:  
    - 데이터베이스 접속 사용자 이름입니다.
    - 예시: `issuer_user`

* `spring.datasource.password`:  
    - 데이터베이스 접속 비밀번호입니다.
    - 예시: `your_secure_password`

### 5.3.3. JPA 설정
* `spring.jpa.open-in-view`: 🔒 
    - OSIV(Open Session In View) 패턴 사용 여부를 설정합니다. true로 설정 시 HTTP 요청 전체에 대해 데이터베이스 연결을 유지합니다. 성능에 영향을 줄 수 있으므로 주의가 필요합니다.
    - 예시: `true`

* `spring.jpa.show-sql`: 🔒 
    - SQL 쿼리 로깅 여부를 설정합니다. true로 설정 시 실행되는 SQL 쿼리를 로그에 출력합니다. 개발 시 디버깅에 유용합니다.
    - 예시: `true`

* `spring.jpa.hibernate.ddl-auto`: 🔒 
    - Hibernate의 DDL 자동 생성 모드를 설정합니다. 데이터베이스 스키마 자동 생성 전략을 지정합니다. 'none'으로 설정 시 자동 생성을 비활성화합니다.
    - 예시: `none`

* `spring.jpa.hibernate.naming.physical-strategy`: 🔒 
    - 데이터베이스 객체 명명 전략을 설정합니다. 엔티티 클래스의 이름을 데이터베이스 테이블 이름으로 변환하는 전략을 지정합니다.
    - 예시: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

* `spring.jpa.properties.hibernate.format_sql`: 🔒 
    - SQL 포맷팅 여부를 설정합니다. false로 설정 시 로그에 출력되는 SQL 쿼리의 포맷팅을 비활성화합니다.
    - 예시: `false`


## 5.4. wallet.yml

### 5.4.1. 월렛 설정
* `wallet.file-path`:  
    - 월렛 파일의 경로를 지정합니다. 파일 월렛을 저장하는 파일의 위치를 지정합니다. 이 파일은 개인키 등 중요한 정보를 포함할 수 있습니다. *반드시 절대경로로 입력해야합니다*
    - 예시: `/path/to/your/issuer.wallet`

* `wallet.password`:  
    - 월렛 접근에 사용되는 비밀번호입니다. 월렛 파일의 접근시 사용되는 비밀번호입니다. 높은 보안이 요구되는 정보입니다.
    - 예시: `your_secure_wallet_password`

## 5.5. issue.yml

### 5.5.1. Issuer 설정
* `issue.did`:
  - Issuer의 분산 식별자(DID)를 지정합니다.
  - DID는 분산 시스템에서 발행자(Issuer)를 고유하게 식별할 수 있는 식별자입니다.
  - 예시: `did:omn:issuer`

* `issue.name`:
  - Issuer의 이름을 지정합니다. 이 이름은 발급 주체를 나타내는 데 사용됩니다.
  - 예시: `issuer`

* `issue.assert-sign-key-id`:
  - Assertion 서명에 사용할 키의 ID를 지정합니다. 이 키는 발급된 인증서나 증명서에 서명하는 데 사용됩니다.
  - 예시: `assert`

* `issue.domain`:
  - Issuer 서버의 도메인을 입력합니다. 이 도메인은 Issue Profile, VC Schema 등에서 사용됩니다.
  - 예시: `http://127.0.0.1:8091/issuer`

* `issue.cert-vc-ref`:
  - Issuer의 가입 증명서(VC)를 조회할 수 있는 주소(URL)를 지정합니다.
  - 이 URL을 통해 해당 Issuer가 발급한 인증서의 진위를 확인할 수 있습니다.
  - 예시: `http://127.0.0.1:8091/issuer/api/v1/certificate-vc`

* `issue.revoke-verify-auth-type`:
  - 인증된 VC를 폐기할 때, Holder의 인증 방법을 설정합니다. 
  - 이 값은 `VerifyAuthType` 열거형(Enum)을 참고하여 설정하며, 여러 인증 방법이 가능합니다.
  - 예시: `PIN_OR_BIO` (PIN 또는 생체 인증을 사용)

* `issue.profiles`:
  - VC(Verifiable Credential) 발급에 사용하는 프로파일을 설정합니다.
  - 서버는 다양한 VC Plan ID를 지원하며, 각 Plan ID에 따라 프로파일이 달라질 수 있습니다.
  - `profile.req-e2e`: Issuer와 Holder 간 통신 시 사용할 E2E(End-to-End) 암호화 방식을 설정합니다.
  - 예시: Domain을 환경에 맞게 변경 후 사용합니다.
    ```yml
    issue:
        profiles:
            VCPLANID000000000001:
                type: IssueProfile
                title: Mobile Driver License
                description: Mobile Driver License
                encoding: UTF-8
                language: ko
                profile:
                    issuer:
                    did: did:omn:issuer
                    name: issuer
                    # Issuer cert VC URL
                    cert-vc-ref: ${issue.cert-vc-ref}  
                    credential-schema:
                    # VC Schema URL
                        id: ${issue.domain}/api/v1/vc/vcschema?name=mdl
                        type: OsdSchemaCredential
                    process:
                        endpoints:
                            - ${issue.domain}
                    req-e2e:
                        curve: Secp256r1
                        cipher: AES-256-CBC
                        padding: PKCS5
    ```


## 5.6. blockchain.properties
- 역할: Issuer 서버에서 연동할 블록체인 서버 정보를 설정합니다. [Open DID Installation Guide]의 '5.1.1. Hyperledger Fabric 테스트 네트워크 설치'에 따라 Hyperledger Fabric 테스트 네트워크를 설치하면, 개인 키, 인증서, 서버 접속 정보 설정 파일이 자동으로 생성됩니다. blockchain.properties에서는 이들 파일이 위치한 경로와, Hyperledger Fabric 테스트 네트워크 설치 시 입력한 네트워크 이름을 설정합니다. 또한, '5.1.2. Open DID 체인코드 배포'에서 배포한 Open DID의 체인코드 이름도 설정합니다.

- 위치: `src/main/resources/properties`

### 5.6.1. 블록체인 연동 설정 

* `fabric.configFilePath:`: 
  - Hyperledger Fabric의 접속 정보 파일이 위치한 경로를 설정합니다. 해당 파일은 Hyperledger Fabric 테스트 네트워크 설치시 자동으로 생성되며, 기본 파일명은 'connection-org1.json' 입니다.
  - 예시: {yourpath}/connection-org1.json

* `fabric.privateKeyFilePath:`: 
  - Hyperledger Fabric의 클라이언트가 네트워크 상에서 트랜잭션 서명과 인증을 위해 사용하는 개인 키 파일 경로를 설정합니다. 해당 파일은 Hyperledger Fabric 테스트 네트워크 설치시 자동으로 생성됩니다.
  - 예시: {yourpath}/{개인키 파일명}

* `fabric.certificateFilePath:`: 
  - Hyperledger Fabric의 클라이언트 인증서가 위치한 경로를 설정합니다. 해당 파일은 Hyperledger Fabric 테스트 네트워크 설치시 자동으로 생성되며, 기본 파일명은 'cert.pem' 입니다.
  - 예시: /etc/hyperledger/fabric/certs/cert.pem

* `fabric.mychannel:`: 
  - Hyperledger Fabric에서 사용하는 프파이빗 네트워크(채널) 이름입니다. Hyperledger Fabric 테스트 네트워크 설치시 입력한 채널명을 설정해야 합니다.
  - 예시: mychannel

* `fabric.chaincodeName:`: 🔒
  - Hyperledger Fabric에서 사용하는 Open DID의 체인코드 이름입니다. 해당 값은 'opendid'로 고정입니다.
  - 예시: opendid

<br/>

# 6. 프로파일 설정 및 사용

## 6.1. 프로파일 개요 (`sample`, `dev`)
Issuer 서버는 다양한 환경에서 실행될 수 있도록 `sample`와 `dev` 두 가지 프로파일을 지원합니다.

각 프로파일은 해당 환경에 맞는 설정을 적용하도록 설계되었습니다. 기본적으로 Issuer 서버는 `sample` 프로파일로 설정되어 있으며, 이 프로파일은 데이터베이스나 블록체인과 같은 외부 서비스와의 연동 없이 서버를 독립적으로 구동할 수 있도록 설계되었습니다. `sample` 프로파일은 API 호출 테스트에 적합하여, 개발자가 애플리케이션의 기본 동작을 빠르게 확인할 수 있도록 지원합니다. 이 프로파일은 모든 API 호출에 대해 고정된 응답 데이터를 반환하므로, 초기 개발환경에서 유용합니다.

샘플 API 호출은 JUnit 테스트로 작성되어 있으므로, 테스트 작성 시 이를 참고할 수 있습니다.

반면, `dev` 프로파일은 실제 동작을 수행하도록 설계되었습니다. 이 프로파일을 사용하면 실데이터에 대한 테스트와 검증이 가능합니다. `dev` 프로파일을 활성화하면 실제 데이터베이스, 블록체인 등 외부 서비스와 연동되어, 실제 환경에서의 애플리케이션 동작을 테스트할 수 있습니다.

### 6.1.1. `sample` 프로파일
`sample` 프로파일은 외부 서비스(DB, 블록체인 등)와의 연동 없이 서버를 독립적으로 구동할 수 있도록 설계되었습니다. 이 프로파일은 API 호출 테스트에 적합하며, 개발자가 애플리케이션의 기본 동작을 빠르게 확인할 수 있습니다. 모든 API 호출에 대해 고정된 응답 데이터를 반환하므로, 초기 개발 단계나 기능 테스트에 유용합니다. 외부 시스템과의 연동이 전혀 필요하지 않기 때문에, 단독으로 서버를 실행하고 테스트할 수 있는 환경을 제공합니다.

### 6.1.2. `dev` 프로파일
`dev` 프로파일은 개발 환경에 적합한 설정을 포함하며, 개발 서버에서 사용됩니다. 이 프로파일을 사용하려면 개발 환경의 데이터베이스와 블록체인 노드에 대한 설정이 필요합니다.


## 6.2. 프로파일 설정 방법
각 구동 방법별로 프로파일을 변경하는 방법을 설명합니다.

### 6.2.1. IDE를 사용한 서버 구동 시
- **설정 파일 선택:** `src/main/resources` 경로에서 `application.yml` 파일을 선택합니다.
- **프로파일 지정:** IDE의 실행 설정(Run/Debug Configurations)에서 `--spring.profiles.active={profile}` 옵션을 추가해 원하는 프로파일을 활성화합니다.
- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

### 6.2.2. 콘솔 명령어를 사용한 서버 구동 시
- **설정 파일 선택:** 빌드된 JAR 파일과 동일한 디렉토리 또는 설정 파일이 위치한 경로에 프로파일별 설정 파일을 준비합니다.
- **프로파일 지정:** 서버 구동 명령어에 `--spring.profiles.active={profile}` 옵션을 추가하여 원하는 프로파일을 활성화합니다.
  
  ```bash
  java -jar build/libs/your-app-name.jar --spring.profiles.active={profile}
  ```

- **설정 적용:** 활성화된 프로파일에 따라 해당 설정 파일이 적용됩니다.

### 6.2.3. Docker를 사용한 서버 구동 시
- **설정 파일 선택:** Docker 이미지 생성 시, Dockerfile에서 설정 파일 경로를 지정하거나, 외부 설정 파일을 Docker 컨테이너에 마운트합니다.
- **프로파일 지정:** Docker Compose 파일 또는 Docker 실행 명령어에서 `SPRING_PROFILES_ACTIVE` 환경 변수를 설정하여 프로파일을 지정합니다.
  
  ```yaml
  environment:
    - SPRING_PROFILES_ACTIVE={profile}
  ```

- **설정 적용:** Docker 컨테이너 실행 시 지정된 프로파일에 따라 설정이 적용됩니다.

각 방법에 따라 프로파일별 설정을 유연하게 변경하여 사용할 수 있으며, 프로젝트 환경에 맞는 설정을 쉽게 적용할 수 있습니다.

# 7. Docker로 빌드 후 구동하기

## 7.1. Docker 이미지 빌드 방법 (`Dockerfile` 기반)
다음 명령어로 Docker 이미지를 빌드합니다:

```bash
docker build -t did-issuer-server .
```

## 7.2. Docker 이미지 실행
빌드된 이미지를 실행합니다:

```bash
docker run -d -p 8091:8091 did-issuer-server
```

## 7.3. Docker Compose를 이용한 구동

### 7.3.1. `docker-compose.yml` 파일 설명
`docker-compose.yml` 파일을 사용하여 여러 컨테이너를 쉽게 관리할 수 있습니다.

```yaml
version: '3'
services:
  app:
    image: did-issuer-server
    ports:
      - "8091:8091"
    volumes:
      - ${your-config-dir}:/app/config
    environment:
      - SPRING_PROFILES_ACTIVE=sample
```

### 7.3.2. 컨테이너 실행 및 관리
다음 명령어로 Docker Compose를 사용해 컨테이너를 실행합니다:

```bash
docker-compose up -d
```

### 7.3.3. 서버 설정 방법
위의 예시에서 `${your-config-dir}` 디렉토리를 컨테이너 내 `/app/config`로 마운트하여 설정 파일을 공유합니다.
- 추가적인 설정이 필요한 경우, 마운트된 폴더에 별도의 property 파일을 추가하여 설정을 변경할 수 있습니다. 
  - 예를 들어, `application.yml` 파일을 `${your-config-dir}`에 추가하고, 이 파일에 변경할 설정을 작성합니다. 
  - `${your-config-dir}`에 위치한 `application.yml` 파일은 기본 설정 파일보다 우선적으로 적용됩니다.
- 자세한 설정은 [5. 설정 가이드](#5-설정-가이드)를 참고합니다.

# 8. Docker PostgreSQL 설치하기

Docker를 사용해 PostgreSQL을 설치하는 방법을 설명합니다. 이 방법을 통해 PostgreSQL을 손쉽게 설치하고, 서버에 연동해 사용할 수 있습니다.

## 8.1. Docker Compose를 이용한 PostgreSQL 설치

다음은 Docker Compose를 이용해 PostgreSQL을 설치하는 방법입니다.

```yml
services:
  postgres:
    container_name: postgre-issuer
    image: postgres:16.4
    restart: always
    volumes:
      - postgres_data_issuer:/var/lib/postgresql/data
    ports:
      - 5431:5432
    environment:
      POSTGRES_USER: ${USER}
      POSTGRES_PASSWORD: ${PW}
      POSTGRES_DB: issuer

volumes:
  postgres_data_issuer:
```

이 Docker Compose 파일은 PostgreSQL 16.4. 버전을 설치하고, 다음과 같은 설정을 합니다:

- **container_name**: 컨테이너 이름을 `postgre-issuer`로 지정합니다.
- **volumes**: `postgres_data_issuer` 볼륨을 PostgreSQL의 데이터 디렉토리(`/var/lib/postgresql/data`)로 마운트합니다. 이를 통해 데이터가 영구적으로 보존됩니다.
- **ports**: 호스트의 5431 포트를 컨테이너의 5432 포트와 매핑합니다.
- **environment**: PostgreSQL의 사용자명, 비밀번호, 데이터베이스 이름을 설정합니다. 여기서 `${USER}`, `${PW}`는 환경 변수로 설정할 수 있습니다.

## 8.2. PostgreSQL 컨테이너 실행

위의 Docker Compose 파일을 사용해 PostgreSQL 컨테이너를 실행하려면, 아래 명령어를 터미널에서 실행합니다:

```bash
docker-compose up -d
```

이 명령어는 백그라운드에서 PostgreSQL 컨테이너를 실행합니다. 설정된 환경 변수에 따라 PostgreSQL 서버가 실행되며, 데이터베이스가 준비됩니다. 이 데이터베이스를 애플리케이션에서 사용할 수 있도록 연동 설정을 진행하면 됩니다.

<!-- References -->
[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/main/docs/guide/installation/OepnDID_Installation_Guide.md