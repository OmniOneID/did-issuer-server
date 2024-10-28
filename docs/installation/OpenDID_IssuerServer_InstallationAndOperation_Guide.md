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

Table of Contents
==
- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. Issuer Server Definition](#12-issuer-server-definition)
  - [1.3. System Requirements](#13-system-requirements)
- [2. Preparation](#2-preparation)
  - [2.1. Git Installation](#21-git-installation)
  - [2.2. PostgreSQL Installation](#22-postgresql-installation)
- [3. Cloning the Source Code from GitHub](#3-cloning-the-source-code-from-github)
  - [3.1. Cloning the Source Code](#31-cloning-the-source-code)
  - [3.2. Directory Structure](#32-directory-structure)
- [4. How to Run the Server](#4-how-to-run-the-server)
  - [4.1. Running with IntelliJ IDEA (Gradle Support)](#41-running-with-intellij-idea-gradle-support)
    - [4.1.1. Installing and Setting Up IntelliJ IDEA](#411-installing-and-setting-up-intellij-idea)
    - [4.1.2. Opening the Project in IntelliJ](#412-opening-the-project-in-intellij)
    - [4.1.3. Gradle Build](#413-gradle-build)
    - [4.1.4. Running the Server](#414-running-the-server)
    - [4.1.5. Database Installation](#415-database-installation)
    - [4.1.6. Server Configuration](#416-server-configuration)
  - [4.2. Running the Server Using Console Commands](#42-running-the-server-using-console-commands)
    - [4.2.1. Gradle Build Command](#421-gradle-build-command)
    - [4.2.2. Running the Server](#422-running-the-server)
    - [4.2.3. Database Installation](#423-database-installation)
    - [4.2.4. Server Configuration](#424-server-configuration)
  - [4.3. Running with Docker](#43-running-with-docker)
- [5. Configuration Guide.](#5-configuration-guide)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring Basic Settings](#511-spring-basic-settings)
    - [5.1.2. Server Setting](#512-server-settings)
    - [5.1.3. TAS Settings](#513-tas-settings)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. Logging Settings](#521-logging-settings)
  - [5.3. database.yml](#53-databaseyml)
    - [5.3.1. Spring Liquibase Settings](#531-spring-liquibase-settings)
    - [5.3.2. Data Source Settings](#532-data-source-settings)
    - [5.3.3. JPA Settings](#533-jpa-settings)
  - [5.4. wallet.yml](#54-walletyml)
    - [5.4.1. Wallet Settings](#541-wallet-settings)
  - [5.5. issue.yml](#55-issueyml)
    - [5.5.1. Issuer Settings](#551-issuer-settings)
  - [5.6. blockchain.properties](#56-blockchainproperties)
    - [5.6.1. Blockchain Integration Settings](#561-blockchain-integration-settings)
- [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)
  - [6.1. Profile Overview (`sample`, `dev`)](#61-profile-overview-sample-dev)
    - [6.1.1. `sample` Profile](#611-sample-profile)
    - [6.1.2. `dev` Profile](#612-dev-profile)
  - [6.2. How to Configure Profiles](#62-how-to-configure-profiles)
    - [6.2.1. Running the Server Using an IDE](#621-running-the-server-using-an-ide)
    - [6.2.2. Running the Server Using Console Commands](#622-running-the-server-using-console-commands)
    - [6.2.3. Running the Server Using Docker](#623-running-the-server-using-docker)
- [7. Running After Building with Docker](#7-running-after-building-with-docker)
  - [7.1. How to Build a Docker Image (Based on `Dockerfile`)](#71-how-to-build-a-docker-image-based-on-dockerfile)
  - [7.2. Running the Docker Image](#72-running-the-docker-image)
  - [7.3. Running with Docker Compose](#73-running-with-docker-compose)
    - [7.3.1. `docker-compose.yml` File Explanation](#731-docker-composeyml-file-explanation)
    - [7.3.2. Running and Managing Containers](#732-running-and-managing-containers)
    - [7.3.3. How to Configure the Server](#733-how-to-configure-the-server)
- [8. Installing PostgreSQL with Docker](#8-installing-postgresql-with-docker)
  - [8.1. Installing PostgreSQL Using Docker Compose](#81-installing-postgresql-using-docker-compose)
  - [8.2. Running the PostgreSQL Container](#82-running-the-postgresql-container)
    

# 1. Introduction

## 1.1. Overview
This document provides a guide for the installation and operation of the Issuer server. It explains the installation process, configuration methods, and operational procedures step by step, guiding users to install and operate it efficiently.

For the complete installation guide of OpenDID, please refer to the [Open DID Installation Guide].

<br/>

## 1.2. Issuer Server Definition
Issuer server provides various functions such as Issue VC, Revoke VC, and status changes within Open DID.

<br/>

## 1.3. System Requirements
- **Java 17** or higher
- **Gradle 7.0** or higher
- **Docker** and **Docker Compose** (when using Docker)
- At least **2GB RAM** and **10GB of disk space**

<br/>

# 2. Preparation

This chapter provides the necessary preparatory steps before installing the components of the Open DID project.

## 2.1. Git Installation

`Git` is a distributed version control system that tracks changes in the source code and supports collaboration among multiple developers. Git is essential for managing the source code and version control of the Open DID project.

After a successful installation, you can check the version of Git with the following command:
```bash
git --version
```

> **Reference Links**
> - [Git Installation Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL Installation
To run the Issuer server, a database installation is required, and Open DID uses PostgreSQL.

> **Reference Links**
> - [PostgreSQL Installation Guide](https://www.postgresql.org/download/)
> - [8. Installing PostgreSQL with Docker](#8-installing-postgresql-with-docker)

<br/>


# 3. Cloning the Source Code from GitHub

## 3.1. Cloning the Source Code

The `git clone` command allows you to copy the source code from a remote repository hosted on GitHub to your local computer. By using this command, you can work on the entire source code and related files locally. After cloning, you can proceed with the necessary tasks within the repository and push any changes back to the remote repository.

Open a terminal and run the following commands to copy the Issuer server repository to your local computer:
```bash
# Clone the repository from the Git repository
git clone https://github.com/OmniOneID/did-issuer-server.git

# Navigate to the cloned repository
cd did-issuer-server
```

> **Reference Links**
> - [Git Clone Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 3.2. Directory Structure
The main directory structure of the cloned project is as follows:

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

| Name                    | Description                                  |
| ----------------------- | -------------------------------------------- |
| CHANGELOG.md            | Changes by version for the project           |
| CODE_OF_CONDUCT.md      | Code of conduct for contributors             |
| CONTRIBUTING.md         | Contribution guidelines and procedures       |
| LICENSE                 | License                                      |
| LICENSE-dependencies.md | License information for project dependencies |
| MAINTAINERS.md          | Guidelines for project maintainers           |
| RELEASE-PROCESS.md      | Procedure for releasing a new version        |
| SECURITY.md             | Security policy and vulnerability reporting  |
| docs                    | Documentation                                |
| ┖ api                   | API guide documents                          |
| ┖ errorCode             | Error codes and troubleshooting guide        |
| ┖ installation          | Installation and configuration guide         |
| ┖ db                    | Database ERD and table specifications        |
| source                  | Source code                                  |
| ┖ did-issuer-server     | Issuer server source code and build files    |
| ┖ gradle                | Gradle build configuration and scripts       |
| ┖ libs                  | External libraries and dependencies          |
| ┖ sample                | Sample files                                 |
| ┖ src                   | Main source code directory                   |
| ┖ build.gradle          | Gradle build configuration file              |
| ┖ README.md             | Source code overview and instructions        |

<br/>


# 4. How to Run the Server
This chapter explains three methods to run the server.

The project source is located under the `source` directory, and depending on the method, you need to load and configure the source from that directory.

1. **Using an IDE**: You can open the project in an Integrated Development Environment (IDE), set up the run configuration, and directly run the server. This method is useful when quickly testing code changes during development.

2. **Using console commands after building**: After building the project, you can run the server by executing the generated JAR file with a console command (`java -jar`). This method is mainly used when deploying or running the server in a production environment.

3. **Building with Docker**: You can build the server as a Docker image and run it as a Docker container. This method ensures consistency across environments and makes deployment and scaling easier.
   
## 4.1. Running with IntelliJ IDEA (Gradle Support)

IntelliJ IDEA is a widely used Integrated Development Environment (IDE) for Java development. It supports build tools like Gradle, making project setup and dependency management very convenient. Since the Open DID server is built using Gradle, you can easily configure and run the server within IntelliJ IDEA.

### 4.1.1. Installing and Setting Up IntelliJ IDEA
1. Install IntelliJ. (Refer to the link below for installation instructions)

> **Reference Links**
> - [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

### 4.1.2. Opening the Project in IntelliJ
- Launch IntelliJ and select `File -> New -> Project` from Existing Sources. When the file selection window appears, choose the 'source/did-issuer-server' folder from the repository cloned in [3.1. Cloning the Source Code](#31-cloning-the-source-code)
- When the project opens, the `build.gradle` file will be automatically recognized.
- Gradle will automatically download the necessary dependency files. Please wait until this process is completed.

### 4.1.3. Gradle Build
- In the Gradle tab in IntelliJ IDEA, run `Tasks -> build -> build`.
- Once the build is successfully completed, the project will be ready for execution.

### 4.1.4. Running the Server
- In the Gradle tab of IntelliJ IDEA, select `Tasks -> application -> bootRun` and run it.
- Gradle will automatically build and run the server.
- Check the console log for the message "Started [ApplicationName] in [time] seconds" to confirm that the server has started successfully.
- Once the server is running properly, open your browser and navigate to http://localhost:8090/swagger-ui/index.html to verify that the API documentation is displayed correctly through Swagger UI.

> **Note**
> - The Issuer server is initially configured to use the `sample` profile.
> - When the sample profile is set, the server will run without required configurations (e.g., database). For more details, refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)

### 4.1.5. Database Installation
The Issuer server stores the necessary data for operation in a database, so a database must be installed to run the server. The Open DID server uses the PostgreSQL database. There are several ways to install the PostgreSQL server, but using Docker is the simplest and easiest method. For PostgreSQL installation instructions, please refer to [2.2. PostgreSQL Installation](#22-postgresql-installation)

<br/>

### 4.1.6. Server Configuration
- The server settings must be modified according to the deployment environment to ensure stable operation. For example, you need to adjust configuration elements such as database connection information, port numbers, email integration details, etc., based on the specific environment.
- The server configuration files are located at `src/main/resource/config.`
- For detailed configuration instructions, please refer to [5. Configuration Guide.](#5-configuration-guide)

<br/>

## 4.2. Running the Server Using Console Commands

This section provides instructions on how to run the Open DID server using console commands. It explains the process of building the project with Gradle and running the server using the generated JAR file.

### 4.2.1. Gradle Build Command

- Build the source using `gradlew`:
  ```shell
    # Navigate to the source folder of the cloned repository
    cd source/did-issuer-server

    # Grant execute permission to the Gradle Wrapper
    chmod 755 ./gradlew

    # Clean build the project (delete previous build files and rebuild)
    ./gradlew clean build
  ```
  > Note
  > - `gradlew` is short for Gradle Wrapper, a script used to run Gradle in a project. Even if Gradle is not installed locally, it automatically downloads and runs the version of Gradle specified by the project, allowing developers to build the project in a consistent environment, regardless of whether Gradle is installed.

- Navigate to the build folder and confirm that the JAR file has been generated:
    ```shell
      cd build/libs
      ls
    ```
- This command generates the file `did-issuer-server-1.0.0.jar`.

<br/>

### 4.2.2. Running the Server
Run the server using the built JAR file:

```bash
java -jar did-issuer-server-1.0.0.jar
```

- Once the server is running properly, open your browser and navigate to http://localhost:8090/swagger-ui/index.html to verify that the API documentation is displayed correctly through Swagger UI.

> **Note**
> - The Issuer server is initially configured to use the `sample` profile.
> - When the sample profile is set, the server will run without required configurations (e.g., database). For more details, refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)

<br/>

### 4.2.3. Database Installation
The Issuer server stores the necessary data for operation in a database, so a database must be installed to run the server. The Open DID server uses the PostgreSQL database. There are several ways to install the PostgreSQL server, but using Docker is the simplest and easiest method. For PostgreSQL installation instructions, please refer to [2.2. PostgreSQL Installation](#22-postgresql-installation)

<br/>

### 4.2.4. Server Configuration
- The server settings must be modified according to the deployment environment to ensure stable operation. For example, you need to adjust configuration elements such as database connection information, port numbers, email integration details, etc., based on the specific environment.
- The server configuration files are located at `src/main/resource/config.`
- For detailed configuration instructions, please refer to [5. Configuration Guide.](#5-configuration-guide)

<br/>\

## 4.3. Running with Docker
- For the process of building, configuring, and running a Docker image, please refer to [7. Running After Building with Docker](#7-running-after-building-with-docker)

<br/>


# 5. Configuration Guide.

This chapter provides guidance on each configuration value included in all of the server's configuration files. Each setting is a crucial element that controls the server's behavior and environment, so proper configuration is necessary for stable server operation. Refer to the descriptions and examples for each item to apply the appropriate settings for your environment.

## 5.1. application.yml

### 5.1.1. Spring Basic Settings
The basic settings in Spring define the application's name and the active profile, playing a critical role in configuring the server's runtime environment.

* `spring.application.name`: 
    - Specifies the name of the application.
    - Purpose: Primarily used for identifying the application in log messages, monitoring tools, or within Spring Cloud services.
    - Example: `Issuer`

* `spring.profiles`:  
    - Defines the profiles and profile groups to be activated.
    - Purpose: 
        - `active`: Specifies the profile that will be activated by default.
        - `group`: Defines the profile groups to be activated by environment (dev, sample).
    - Example configuration and explanation:
      ```yaml
      profiles:
        active: dev
        group:
          dev:            
            - databases   # Database related settings (application-database.yml)
            - wallet      # Wallet related settings (application-wallet.yml)
            - logging     # Logging settings (application-logging.yml)
            - spring-docs # Swagger API documentation settings (application-spring-docs.yml, optional)
            - issue       # Issuer server settings (application-issue.yml)            
      ```

* `spring.jackson`: 🔒 
    - Settings related to JSON serialization/deserialization. Commonly used values during communication with other servers.
    - Example:
      ```yaml
      default-property-inclusion: non_null
      serialization:
        fail-on-empty-beans: false
      ```

### 5.1.2. Server Settings 
* `server.port`:  
    - The port number on which the application will run. The default port setting for the Issuer server is 8091.
    - Value: 8091

### 5.1.3. TAS Settings 
The Issuer service communicates with the TAS server. You just need to set the address of the TAS server you built.
* `tas.url`:  
    - The URL of the TAS (Trust Anchor Service). It can be used for authentication or trust verification.
    - Example: `http://localhost:8090/contextpath/tas`

## 5.2. application-logging.yml

### 5.2.1. Logging Settings
* `logging.level`: 
    - Sets the log level.
    - By setting the level to debug, you can see all log messages at DEBUG level or higher (INFO, WARN, ERROR, FATAL) for the specified package.

Full example:
```yaml
logging:
  level:
    org.omnione: debug
```

## 5.3. database.yml

### 5.3.1. Spring Liquibase Settings 
* `spring.liquibase.change-log`: 🔒 
    - Specifies the location of the database change log file. This is the log file used by Liquibase to track and apply changes to the database schema.
    - Example: `classpath:/db/changelog/master.xml`

* `spring.liquibase.enabled`: 🔒 
    - Sets whether Liquibase is enabled. If set to true, Liquibase will run at application startup to perform database migration.
    - Example: `true` [dev], `false` [sample]

* `spring.liquibase.fall-on-error`: 🔒 
    - Controls the behavior when an error occurs during Liquibase's database migration. It is set only in sample.
    - Example: `false` [sample]

### 5.3.2. Data Source Settings
* `spring.datasource.driver-class-name`:  
    - Specifies the database driver class to be used. It designates the JDBC driver for connecting to the database. Currently written with PostgreSQL as a reference.
    - Example: `org.postgresql.Driver`

* `spring.datasource.url`:  
    - The database connection URL. It specifies the location and name of the database the application will connect to. It is written with PostgreSQL as a reference.
    - Example: `jdbc:postgresql://localhost:5432/issuer_db`

* `spring.datasource.username`:  
    - The username for database access.
    - Example: `issuer_user`

* `spring.datasource.password`:  
    - The password for database access.
    - Example: `your_secure_password`

### 5.3.3. JPA Settings
* `spring.jpa.open-in-view`: 🔒 
    - Sets whether to use the OSIV (Open Session In View) pattern. If set to true, it maintains a database connection for the entire HTTP request. Care must be taken as it may affect performance.
    - Example: `true`

* `spring.jpa.show-sql`: 🔒 
    - Sets whether to log SQL queries. If set to true, it outputs the executed SQL queries to the log. It is useful for debugging during development.
    - Example: `true`

* `spring.jpa.hibernate.ddl-auto`: 🔒 
    - Sets the DDL auto generation mode of Hibernate. It specifies the strategy for automatic generation of the database schema. Setting it to 'none' disables automatic generation.
    - Example: `none`

* `spring.jpa.hibernate.naming.physical-strategy`: 🔒 
    - Sets the naming strategy for database objects. It specifies the strategy for converting entity class names into database table names.
    - Example: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

* `spring.jpa.properties.hibernate.format_sql`: 🔒 
    - Sets whether to format SQL. If set to false, it disables formatting of the SQL queries output in the logs.
    - Example: `false`


## 5.4. wallet.yml

### 5.4.1. Wallet Settings
* `wallet.file-path`:  
    - Specifies the path of the wallet file. It designates the location of the file storing the wallet. This file may contain sensitive information such as private keys. *Must be entered as an absolute path*
    - Example: `/path/to/your/issuer.wallet`

* `wallet.password`:  
    - The password used to access the wallet. This password is used when accessing the wallet file. It is highly sensitive information.
    - Example: `your_secure_wallet_password`

## 5.5. issue.yml

### 5.5.1. Issuer Settings
* `issue.did`:
  - Specifies the decentralized identifier (DID) of the Issuer.
  - DID is an identifier that uniquely identifies the Issuer in a distributed system.
  - Example: `did:omn:issuer`

* `issue.name`:
  - Specifies the name of the Issuer. This name is used to represent the issuing entity.
  - Example: `issuer`

* `issue.assert-sign-key-id`:
  - Specifies the ID of the key to be used for assertion signing. This key is used to sign issued certificates or proofs.
  - Example: `assert`

* `issue.domain`:
  - Inputs the domain of the Issuer server. This domain is used in Issue Profile, VC Schema, etc.
  - Example: `http://127.0.0.1:8091/issuer`

* `issue.cert-vc-ref`:
  - Specifies the address (URL) where the Issuer's credential verification (VC) can be retrieved.
  - Through this URL, the authenticity of the certificates issued by that Issuer can be verified.
  - Example: `http://127.0.0.1:8091/issuer/api/v1/certificate-vc`

* `issue.revoke-verify-auth-type`:
  - Sets the authentication method of the Holder when revoking the authenticated VC. 
  - This value is configured with reference to the `VerifyAuthType` enumeration (Enum), and multiple authentication methods are possible.
  - Example: `PIN_OR_BIO` (using PIN or biometric authentication)

* `issue.profiles`:
  - Sets the profiles used for issuing VC (Verifiable Credential).
  - The server supports various VC Plan IDs, and the profiles may vary based on each Plan ID.
  - `profile.req-e2e`: Sets the E2E (End-to-End) encryption method used during communication between Issuer and Holder.
  - Example: Use it after changing the domain to suit the environment.
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
- Role: Configures the blockchain server information for integration with the Issuer server. After installing the Hyperledger Fabric test network according to section '5.1.1. Installing the Hyperledger Fabric Test Network' in the [Open DID Installation Guide], files for the private key, certificates, and server connection information will be automatically generated. In the blockchain.properties file, you need to set the paths to these files and specify the network name that was used during the installation of the Hyperledger Fabric test network. Additionally, you will configure the chaincode name for Open DID that was deployed in '5.1.2. Deploying the Open DID Chaincode'.

- Location: `src/main/resources/properties`

### 5.6.1. Blockchain Integration Settings

* `fabric.configFilePath:`: 
  - Specifies the path to the connection information file for Hyperledger Fabric. This file is automatically generated when the Hyperledger Fabric test network is installed, and the default filename is 'connection-org1.json'
  - Example: {yourpath}/connection-org1.json

* `fabric.privateKeyFilePath:`: 
  - Specifies the path to the private key file used by the Hyperledger Fabric client for signing transactions and authentication on the network. This file is automatically generated during the installation of the Hyperledger Fabric test netw
  - Example: {yourpath}/{private key filename}

* `fabric.certificateFilePath:`: 
  - Specifies the path to the client certificate for Hyperledger Fabric. This file is automatically generated when the Hyperledger Fabric test network is installed, and the default filename is 'cert.pem' 
  - Example: {yourpath}/cert.pem

* `fabric.mychannel:`: 
  - The name of the private network (channel) used in Hyperledger Fabric. You must set this to the channel name entered during the installation of the Hyperledger Fabric test network.
  - Example: mychannel

* `fabric.chaincodeName:`: 🔒
  - The name of the Open DID chaincode used in Hyperledger Fabric. This value is fixed as opendid.
  - Example: opendid

<br/>

# 6. Profile Configuration and Usage

## 6.1. Profile Overview (`sample`, `dev`)
The Issuer server supports two profiles, `sample` and `dev`, to allow operation in various environments.

Each profile is designed to apply the appropriate settings for its environment. By default, the Issuer server is configured to use the `sample` profile, which is designed to run the server independently without connecting to external services such as a database or blockchain. The `sample` profile is ideal for testing API calls, allowing developers to quickly verify the basic functionality of the application. This profile returns fixed response data for all API calls, making it useful in early development environments.

Sample API calls are written as JUnit tests, which can be referenced when writing your own tests.

On the other hand, the `dev` profile is designed to perform actual operations. When using this profile, testing and validation with real data are possible. Activating the dev profile enables integration with external services such as databases and blockchain, allowing you to test the application's behavior in a real-world environment.

### 6.1.1. `sample` Profile
The `sample` profile is designed to run the server independently without connecting to external services (e.g., databases, blockchain). This profile is suitable for testing API calls, allowing developers to quickly verify the basic functionality of the application. It returns fixed response data for all API calls, making it useful in the early development stage or for functional testing. Since no integration with external systems is required, it provides an environment where the server can be run and tested standalone.

### 6.1.2. `dev` Profile
The `dev` profile includes settings suited for the development environment and is used on development servers. To use this profile, you will need configuration for the development environment's database and blockchain node.

## 6.2. How to Configure Profiles
This section explains how to switch profiles for each server operation method.

### 6.2.1. Running the Server Using an IDE
- **Select Configuration File:**  In the `src/main/resources path`, select the `application.yml` file.
- **Specify Profile:** In the IDE's Run/Debug Configurations, add the option `--spring.profiles.active={profile}` to activate the desired profile.
- **Apply Configuration:** The configuration file corresponding to the activated profile will be applied.

### 6.2.2. Running the Server Using Console Commands
- **Select Configuration File:** Prepare the profile-specific configuration files in the same directory as the built JAR file or in the directory where the configuration files are located.
- **Specify Profile:** Add the option `--spring.profiles.active={profile}` to the server startup command to activate the desired profile.
  
  ```bash
  java -jar build/libs/did-issuer-server-1.0.0.jar --spring.profiles.active={profile}
  ```

- **Apply Configuration:** The configuration file corresponding to the activated profile will be applied.

### 6.2.3. Running the Server Using Docker
- **Select Configuration File:** When creating a Docker image, specify the configuration file path in the Dockerfile, or mount external configuration files to the Docker container.
- **Specify Profile:** Set the `SPRING_PROFILES_ACTIVE` environment variable in the Docker Compose file or the Docker run command to specify the profile.
  
  ```yaml
  environment:
    - SPRING_PROFILES_ACTIVE={profile}
  ```

- **Apply Configuration:** The specified profile's configuration will be applied when the Docker container is executed.

By following these methods, you can flexibly switch profile-specific settings and easily apply the appropriate configuration for your project environment.

# 7. Running After Building with Docker

## 7.1. How to Build a Docker Image (Based on `Dockerfile`)
Build the Docker image using the following command:
```bash
docker build -t did-issuer-server .
```

## 7.2. Running the Docker Image
Run the built image with the following command:

```bash
docker run -d -p 8091:8091 did-issuer-server
```

## 7.3. Running with Docker Compose

### 7.3.1. `docker-compose.yml` File Explanation
The `docker-compose.yml` file allows you to easily manage multiple containers.

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

### 7.3.2. Running and Managing Containers
Run the container using Docker Compose with the following command:

```bash
docker-compose up -d
```

### 7.3.3. How to Configure the Server
In the example above, the `${your-config-dir}` directory is mounted to `/app/config` inside the container to share configuration files.

- If additional configuration is required, you can modify settings by adding separate property files to the mounted folder.
For example, add an application.yml file to `${your-config-dir}` and write the necessary configuration changes in this file.
The `application.yml` file located in `${your-config-dir}` will take precedence over the default configuration files.
- For detailed configuration instructions, please refer to [5. Configuration Guide.](#5-configuration-guide)

# 8. Installing PostgreSQL with Docker

This section explains how to install PostgreSQL using Docker. By following this method, you can easily install PostgreSQL and integrate it with your server for use.

## 8.1. Installing PostgreSQL Using Docker Compose

The following is a method to install PostgreSQL using Docker Compose:

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

This Docker Compose file installs PostgreSQL version 16.4 and applies the following settings:

-  **container_name**: Sets the container name to `postgre-issuer`.
- **volumes**: Mounts the `postgres_data_issuer` volume to PostgreSQL's data directory (`/var/lib/postgresql/data`), ensuring data persistence.
- **ports**: Maps port 5431 on the host to port 5432 on the container.
- **environment**: onfigures the PostgreSQL username, password, and database name. `${USER}` and `${PW}` can be set as environment variables.

## 8.2. Running the PostgreSQL Container

To run the PostgreSQL container using the Docker Compose file above, execute the following command in your terminal:

```bash
docker-compose up -d
```

This command runs the PostgreSQL container in the background. The PostgreSQL server will start based on the configured environment variables, and the database will be prepared. You can proceed with the integration settings to allow your application to use this database.

<!-- References -->
[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/main/docs/guide/installation/OepnDID_Installation_Guide.md