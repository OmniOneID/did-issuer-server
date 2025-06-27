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

Open DID Issuer Server Installation Guide
==

- Date: 2025-05-29
- Version: v2.0.0

Table of Contents
==

- [Open DID Issuer Server Installation Guide](#open-did-issuer-server-installation-guide)
- [Table of Contents](#table-of-contents)
- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. Issuer Server Definition](#12-issuer-server-definition)
  - [1.3. System Requirements](#13-system-requirements)
- [2. Prerequisites](#2-prerequisites)
  - [2.1. Git Installation](#21-git-installation)
  - [2.2. PostgreSQL Installation](#22-postgresql-installation)
  - [2.3. Node.js Installation](#23-nodejs-installation)
- [3. Cloning Source Code from GitHub](#3-cloning-source-code-from-github)
  - [3.1. Source Code Cloning](#31-source-code-cloning)
  - [3.2. Directory Structure](#32-directory-structure)
- [4. Server Operation Methods](#4-server-operation-methods)
  - [4.1. Running with IDE (Gradle and React Project Execution)](#41-running-with-ide-gradle-and-react-project-execution)
    - [4.1.1. Running Backend (Spring Boot) in IntelliJ IDEA](#411-running-backend-spring-boot-in-intellij-idea)
      - [1. Install IntelliJ IDEA](#1-install-intellij-idea)
      - [2. Open Project](#2-open-project)
      - [3. Gradle Build](#3-gradle-build)
      - [4. Run Server](#4-run-server)
      - [5. Database Installation](#5-database-installation)
      - [6. Server Configuration](#6-server-configuration)
    - [4.1.2. Running Frontend (React) in VS Code](#412-running-frontend-react-in-vs-code)
      - [1. Install VS Code](#1-install-vs-code)
      - [2. Open Project](#2-open-project-1)
      - [3. Install Dependencies](#3-install-dependencies)
      - [4. Run Development Server](#4-run-development-server)
  - [4.2. Running with Console Commands](#42-running-with-console-commands)
    - [4.2.1. Gradle Build Commands](#421-gradle-build-commands)
    - [4.2.2. Server Operation Method](#422-server-operation-method)
    - [4.2.3. Database Installation](#423-database-installation)
    - [4.2.4. Server Configuration Method](#424-server-configuration-method)
  - [4.3. Running with Docker](#43-running-with-docker)
- [5. Configuration Guide](#5-configuration-guide)
  - [5.1. application.yml](#51-applicationyml)
    - [5.1.1. Spring Basic Configuration](#511-spring-basic-configuration)
    - [5.1.2. Server Configuration](#512-server-configuration)
  - [5.2. application-logging.yml](#52-application-loggingyml)
    - [5.2.1. Logging Configuration](#521-logging-configuration)
  - [5.3. database.yml](#53-databaseyml)
    - [5.3.1. Spring Liquibase Configuration](#531-spring-liquibase-configuration)
    - [5.3.2. Datasource Configuration](#532-datasource-configuration)
    - [5.3.3. JPA Configuration](#533-jpa-configuration)
  - [5.4. wallet.yml](#54-walletyml)
    - [5.4.1. Wallet Configuration](#541-wallet-configuration)
  - [5.5. blockchain.yml](#55-blockchainyml)
  - [5.5.1. Blockchain Configuration](#551-blockchain-configuration)
  - [5.6. blockchain.properties](#56-blockchainproperties)
    - [5.6.1. Blockchain Integration Configuration](#561-blockchain-integration-configuration)
      - [EVM Network Configuration](#evm-network-configuration)
      - [EVM Contract Configuration](#evm-contract-configuration)
- [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)
  - [6.1. Profile Overview (`sample`, `dev`)](#61-profile-overview-sample-dev)
    - [6.1.1. `sample` Profile](#611-sample-profile)
    - [6.1.2. `dev` Profile](#612-dev-profile)
  - [6.2. Profile Configuration Methods](#62-profile-configuration-methods)
    - [6.2.1. When Running Server Using IDE](#621-when-running-server-using-ide)
    - [6.2.2. When Running Server Using Console Commands](#622-when-running-server-using-console-commands)
    - [6.2.3. When Running Server Using Docker](#623-when-running-server-using-docker)
- [7. Running After Building with Docker](#7-running-after-building-with-docker)
  - [7.1. Docker Image Build Method (Based on `Dockerfile`)](#71-docker-image-build-method-based-on-dockerfile)
    - [7.1.1. Build Docker image](#711-build-docker-image)
  - [7.2. Running with Docker Compose](#72-running-with-docker-compose)
    - [7.2.1. Preparing Directories and Configuration Files](#721-preparing-directories-and-configuration-files)
      - [1. Create docker-compose directory and config directory](#1-create-docker-compose-directory-and-config-directory)
      - [2. Copy configuration files (yml) to config directory](#2-copy-configuration-files-yml-to-config-directory)
      - [3. Modify blockchain.properties file](#3-modify-blockchainproperties-file)
      - [4. Modify application-database.yml file](#4-modify-application-databaseyml-file)
    - [7.2.2. Create `docker-compose.yml` file](#722-create-docker-composeyml-file)
    - [7.2.3. Run Container](#723-run-container)
- [8. Installing Docker PostgreSQL](#8-installing-docker-postgresql)
  - [8.1. PostgreSQL Installation Using Docker Compose](#81-postgresql-installation-using-docker-compose)
  - [8.2. Running PostgreSQL Container](#82-running-postgresql-container)
    
# 1. Introduction

## 1.1. Overview
This document provides a guide on the installation, configuration, and operation of the Open DID Issuer server. The Issuer server consists of a Spring Boot-based backend and a React-based Admin console frontend, enabling integrated deployment through Gradle builds. It includes step-by-step explanations of the installation process, environment configuration, Docker execution methods, and profile settings to help users efficiently install and run the server.

- For a complete installation guide for OpenDID, please refer to the [Open DID Installation Guide].
- For the Admin console guide, please refer to the [Open DID Admin Console Guide].


## 1.2. Issuer Server Definition

The Issuer server provides functions such as VC (Verifiable Credential) Schema definition, VC issuance, revocation, status changes, and ZKP Credential definition within the Open DID system.

<br/>

## 1.3. System Requirements
- **Java 21** or higher
- **Gradle 7.0** or higher
- **Docker** and **Docker Compose** (when using Docker)
- Minimum **2GB RAM** and **10GB disk space**

<br/>

# 2. Prerequisites

This chapter provides guidance on the prerequisite items needed before installing the components of the Open DID project.

## 2.1. Git Installation

`Git` is a distributed version control system that tracks changes in source code and supports collaboration among multiple developers. Git is essential for managing the source code of the Open DID project and version control.

If the installation is successful, you can verify Git's version using the following command:

```bash
git --version
```

> **Reference Links**
> - [Git Installation Guide](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository)

<br/>

## 2.2. PostgreSQL Installation
To run the Issuer server, database installation is required, and Open DID uses PostgreSQL.

> **Reference Links**
- [PostgreSQL Installation Guide Documentation](https://www.postgresql.org/download/)
- [8. Installing Docker PostgreSQL](#8-installing-docker-postgresql)

## 2.3. Node.js Installation
To run the React-based Issuer Admin Console, `Node.js` and `npm` are required.

npm (Node Package Manager) is used to install and manage dependencies needed for frontend development.

Once installation is complete, you can verify that it was installed correctly with the following commands:

```bash
node --version
npm --version
```

> **Reference Links**  
> - [Node.js Official Download Page](https://nodejs.org/)  
> - LTS (Long Term Support) version installation is recommended.  

> ✅ Installation Verification Tip  
> If version information is displayed when you enter the `node -v` and `npm -v` commands, it has been installed correctly.

# 3. Cloning Source Code from GitHub

## 3.1. Source Code Cloning

The `git clone` command clones source code from a remote repository hosted on GitHub to your local computer. Using this command allows you to work with the project's entire source code and related files locally. After cloning, you can proceed with necessary work within the repository, and changes can be pushed back to the remote repository.

Open a terminal and execute the following command to copy the Issuer server repository to your local computer.
```bash
# Clone repository from Git repository
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
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   ├── api
│       └── Issuer_API.md
│   ├── errorCode
│       └── Issuer_ErrorCode.md
│   ├── installation
│       └── OpenDID_IssuerServer_InstallationAndOperation_Guide.md
│   └── db
│       └── OpenDID_TableDefinition_Issuer.md
└── source
   └── did-issuer-server
       ├── gradle
       ├── libs
           └── did-sdk-common-2.0.0.jar
           └── did-blockchain-sdk-server-2.0.0.jar
           └── did-core-sdk-server-2.0.0.jar
           └── did-crypto-sdk-server-2.0.0.jar
           └── did-datamodel-server-2.0.0.jar
           └── did-wallet-sdk-server-2.0.0.jar
           └── did-zkp-sdk-server-2.0.0.jar
       ├── sample
       └── src
       └── build.gradle
       └── README.md
   └── did-issuer-admin
```

| Name                    | Description                              |
| ----------------------- | ---------------------------------------- |
| CHANGELOG.md            | Version-specific changes in the project              |
| CODE_OF_CONDUCT.md      | Code of conduct for contributors                |
| CONTRIBUTING.md         | Contribution guidelines and procedures                        |
| LICENSE                 | License |
| dependencies-license.md | License information of project dependency libraries |
| MAINTAINERS.md          | Guidelines for project maintainers              |
| RELEASE-PROCESS.md      | Procedures for releasing new versions            |
| SECURITY.md             | Security policies and vulnerability reporting methods            |
| docs                    | Documentation                                     |
| ┖ api                   | API guide documentation                          |
| ┖ errorCode             | Error codes and troubleshooting guides            |
| ┖ installation          | Installation and configuration guides                      |
| ┖ db                    | Database ERD, table specifications          |
| source/did-issuer-server| Issuer server source code and build files |
| ┖ gradle                | Gradle build configuration and scripts             |
| ┖ libs                  | External libraries and dependencies                |
| ┖ sample                | Sample files                                |
| ┖ src                   | Main source code directory                  |
| ┖ build.gradle          | Gradle build configuration file                    |
| ┖ README.md             | Source code overview and guidance                   |
| source/did-issuer-admin| Issuer Admin console source code |

<br/><br/>

# 4. Server Operation Methods
This chapter provides guidance on three methods for running the server.

Project sources are located under the `source` directory, and sources must be loaded and configured from the corresponding directory according to each operation method.

1. **Using IDE**: You can open the project in an Integrated Development Environment (IDE), configure execution settings, and run the server directly. This method is useful for quickly testing code changes during development.

2. **Using console commands after Build**: After building the project, you can run the server by executing the generated JAR file with console commands (`java -jar`). This method is mainly used when deploying the server or running it in a production environment.

3. **Building with Docker**: You can build the server as a Docker image and run it as a Docker container. This method maintains consistency across environments and has advantages in deployment and scaling.

## 4.1. Running with IDE (Gradle and React Project Execution)

The Open DID project consists of a backend (Spring Boot-based) and frontend (React-based), which can be developed and executed in IntelliJ IDEA and VS Code respectively.

### 4.1.1. Running Backend (Spring Boot) in IntelliJ IDEA

IntelliJ IDEA is an IDE widely used for Java development and is well compatible with Gradle-based projects. Since the Open DID server uses Gradle, it can be easily executed in IntelliJ.

#### 1. Install IntelliJ IDEA

- [Download IntelliJ IDEA](https://www.jetbrains.com/idea/download/)

#### 2. Open Project

- Select `File -> New -> Project from Existing Sources`  
- Select `source/did-issuer-server` directory  
- The `build.gradle` file is automatically recognized and necessary dependencies are automatically downloaded

#### 3. Gradle Build

- Execute `Tasks -> build -> build` from the `Gradle` tab

#### 4. Run Server

- Execute `Tasks -> application -> bootRun`  
- When the message `"Started [ApplicationName] in [time] seconds"` appears in the console, it's running normally

> ⚠️ Runs with default `sample` profile. Operates for testing without database  
> For details, refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage)

#### 5. Database Installation

- Uses PostgreSQL (Docker installation recommended)  
- For detailed installation methods, refer to [2.2. PostgreSQL Installation](#22-postgresql-installation)

#### 6. Server Configuration

- Configuration file location: `src/main/resources/config`  
- Examples: DB connection information, ports, email settings, etc.  
- For detailed configuration methods, refer to [5. Configuration Guide](#5-configuration-guide)

---

### 4.1.2. Running Frontend (React) in VS Code

The Issuer admin console is React-based and can be run separately in VS Code. This is useful for frontend development or UI verification.

#### 1. Install VS Code

- [Download VS Code](https://code.visualstudio.com/)

#### 2. Open Project

- Open `source/did-issuer-admin` directory in VS Code

#### 3. Install Dependencies

```bash
npm install
```

#### 4. Run Development Server

```bash
npm run dev
```

- Default access URL: [http://localhost:5173](http://localhost:5173)

> 📌 **Note:**  
> The backend (Spring Boot server) must be running separately,  
> and the API server address in the frontend can be specified through the `vite.config.ts` file or configuration files.
  

## 4.2. Running with Console Commands

This section provides guidance on running the Open DID server using console commands. It explains the process of building the project using Gradle and running the server using the generated JAR file.
- When building with Gradle, the frontend (Admin Console) is automatically built together and included as static resources.

### 4.2.1. Gradle Build Commands

- Build the source using gradlew.
 ```shell
   # Navigate to the source folder of the cloned repository
   cd source/did-issuer-server

   # Grant execution permission to Gradle Wrapper
   chmod 755 ./gradlew

   # Clean build the project (delete previous build files and build anew)
   ./gradlew clean build
 ```
 > Note: If frontend build is not needed (e.g., when testing only the backend or when frontend artifacts are already included), you can skip frontend build by adding the following option: 
 > - `./gradlew clean build -DskipFrontendBuild=true`


- Navigate to the built folder and confirm that the JAR file has been created.
   ```shell
     cd build/libs
     ls
   ```
- This command creates a `did-issuer-server-2.0.0.jar` file.

<br/>

### 4.2.2. Server Operation Method
Run the server using the built JAR file:

```bash
java -jar did-issuer-server-2.0.0.jar
```

> **Caution**
> - The Issuer server is initially configured with the sample profile.
> - With the sample profile setting, the server runs while ignoring essential configurations (e.g., database). For details, please refer to [6. Profile Configuration and Usage](#6-profile-configuration-and-usage).

<br/>

### 4.2.3. Database Installation
Since the Issuer server stores data necessary for operation in a database, a database must be installed to operate the server. Open DID servers use PostgreSQL database. There are several ways to install PostgreSQL server, but installation using Docker is the most convenient and easy. For PostgreSQL installation methods, please refer to [2.2. PostgreSQL Installation](#22-postgresql-installation).

<br/>

### 4.2.4. Server Configuration Method
- The server must be configured appropriately for the deployment environment to ensure stable operation. For example, configuration components such as database connection information, port numbers, and email integration information must be adjusted for each environment.
- Server configuration files are located in the `src/main/resource/config` path.
- For detailed configuration methods, please refer to [5. Configuration Guide](#5-configuration-guide).

<br/>

## 4.3. Running with Docker
- For Docker image building, configuration, execution, and other processes, please refer to [7. Building and Running with Docker](#7-building-and-running-with-docker) below.

<br/>

# 5. Configuration Guide
This chapter provides guidance on each configuration value included in all server configuration files. Each setting is an important element that controls the operation and environment of the server, and appropriate configuration is necessary for stable server operation. Please refer to the item-by-item explanations and examples to apply settings suitable for each environment.

Settings with the 🔒 icon are values that are fixed by default or generally do not need to be modified.

## 5.1. application.yml

### 5.1.1. Spring Basic Configuration
* `spring.application.name`: 
   - Specifies the name of the application.
   - Purpose: Used by other services to identify this application.
   - Example: `Issuer`

* `spring.profiles`:  
   - Defines profiles to activate and profile groups.
   - Purpose: 
       - `active`: Specifies the profile to be activated by default.
       - `group`: Defines profile groups to be activated by environment (dev, sample).
   - Configuration value examples and explanations:
     ```yaml
     profiles:
       active: dev
       group:
         dev:            
           - databases   # Database related settings (application-database.yml)
           - wallet      # Wallet related settings (application-wallet.yml)
           - logging     # Logging settings (application-logging.yml)
           - spring-docs # Swagger API documentation settings (application-spring-docs.yml, optional)
           - blockchain  # Blockchain property path settings (application-blockchain.yml)
     ```

* `spring.jackson`: 🔒 
   - JSON serialization/deserialization related settings. Values commonly used for communication with other servers.
   - Example:
     ```yaml
     default-property-inclusion: non_null
     serialization:
       fail-on-empty-beans: false
     ```

### 5.1.2. Server Configuration 
* `server.port`:  
   - Port number where the application will run. The default value for Issuer server port configuration is 8091.
   - Value: 8091

## 5.2. application-logging.yml

### 5.2.1. Logging Configuration
* `logging.level`: 
   - Sets the log level.
   - By setting the level to debug, you can see all log messages at DEBUG level and above (INFO, WARN, ERROR, FATAL) for the specified package.

Complete example:
```yaml
logging:
 level:
   org.omnione: debug
```

## 5.3. database.yml

### 5.3.1. Spring Liquibase Configuration 
* `spring.liquibase.change-log`: 🔒 
   - Specifies the location of the database change log file. This is the location of the log file that Liquibase uses to track and apply database schema changes.
   - Example: `classpath:/db/changelog/master.xml`

* `spring.liquibase.enabled`: 🔒 
   - Sets whether to enable Liquibase. When set to true, Liquibase runs during application startup to perform database migration.
   - Example: `true` [dev], `false` [sample]

* `spring.liquibase.fall-on-error`: 🔒 
   - Controls the behavior when an error occurs while Liquibase performs database migration. Set only in sample.
   - Example: `false` [sample]

### 5.3.2. Datasource Configuration
* `spring.datasource.driver-class-name`:  
   - Specifies the database driver class to use. Specifies the JDBC driver for connecting to the database. Currently written based on PostgreSQL.
   - Example: `org.postgresql.Driver`

* `spring.datasource.url`:  
   - Database connection URL. Specifies the location and name of the database the application will connect to. Written based on PostgreSQL.
   - Example: `jdbc:postgresql://localhost:5432/issuer_db`

* `spring.datasource.username`:  
   - Database access username.
   - Example: `issuer_user`

* `spring.datasource.password`:  
   - Database access password.
   - Example: `your_secure_password`

### 5.3.3. JPA Configuration
* `spring.jpa.open-in-view`: 🔒 
   - Sets whether to use the OSIV (Open Session In View) pattern. When set to true, maintains database connection for the entire HTTP request. Caution is needed as it can affect performance.
   - Example: `true`

* `spring.jpa.show-sql`: 🔒 
   - Sets whether to log SQL queries. When set to true, outputs executed SQL queries to logs. Useful for debugging during development.
   - Example: `true`

* `spring.jpa.hibernate.ddl-auto`: 🔒 
   - Sets Hibernate's DDL automatic generation mode. Specifies the database schema automatic generation strategy. When set to 'none', disables automatic generation.
   - Example: `none`

* `spring.jpa.hibernate.naming.physical-strategy`: 🔒 
   - Sets the database object naming strategy. Specifies the strategy for converting entity class names to database table names.
   - Example: `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`

* `spring.jpa.properties.hibernate.format_sql`: 🔒 
   - Sets whether to format SQL. When set to false, disables formatting of SQL queries output to logs.
   - Example: `false`


## 5.4. wallet.yml

### 5.4.1. Wallet Configuration
* `wallet.file-path`:  
   - Specifies the path of the wallet file. Specifies the location of the file where the file wallet is stored. This file may contain important information such as private keys. *Must be entered as an absolute path*
   - Example: `/path/to/your/issuer.wallet`

* `wallet.password`:  
   - Password used for wallet access. Password used when accessing the wallet file. Information that requires high security.
   - Example: `your_secure_wallet_password`

## 5.5. blockchain.yml
## 5.5.1. Blockchain Configuration
* `blockchain.file-path`:
   - Sets the location of the [5.6. blockchain.properties](#56-blockchainproperties) file.
   - Example: `/path/to/your/blockchain.properties`

## 5.6. blockchain.properties
- Role: Configures blockchain server information to be integrated with the Issuer server. When you install the Hyperledger Besu network according to '5.3. Step 3: Blockchain Installation' in the [Open DID Installation Guide], private keys, certificates, and server access information configuration files are automatically generated. In blockchain.properties, you configure the paths where these files are located and the network name entered during Hyperledger Besu installation.

- Location: `src/main/resources/properties`

### 5.6.1. Blockchain Integration Configuration 
#### EVM Network Configuration

- `evm.network.url:`:
  - EVM Network address. When running Besu locally on the same client, use this value as fixed. (Default Port: 8545)
  - Example: http://localhost:8545

- `evm.chainId:`:
  - Chain ID identifier. Currently using a fixed value of 1337. (Default Value: 1337)
  - Example: 1337

- `evm.gas.limit:`:
  - Maximum allowed gas limit for Hyperledger Besu EVM transactions. Currently used as fixed for Free Gas. (Default Value: 100000000)
  - Example: 100000000

- `evm.gas.price :`:
  - Unit gas price. Currently used as fixed at 0 for Free Gas. (Default Value: 0)
  - Example: 0

- `evm.connection.timeout:`: 
  - Network connection timeout value (milliseconds). Currently using the recommended value of 10000 as fixed. (Default Value: 10000)
  - Example: 10000

#### EVM Contract Configuration

- `evm.connection.address:`: 
  - OpenDID Contract Address value returned when deploying Smart Contract with Hardhat. For detailed guide, please refer to [DID Besu Contract].
  - Example: 0xa0E49611FB410c00f425E83A4240e1681c51DDf4

- `evm.connection.privateKey:`: 
  - k1 key used for API access control. Enter the key string defined in accounts inside hardhat.config.js (removing the 0x string prefix) to enable API calls with Owner permissions (Default setting). For detailed guide, please refer to [DID Besu Contract].
  - Example: 0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63

<br/><br/>

# 6. Profile Configuration and Usage

## 6.1. Profile Overview (`sample`, `dev`)
The Issuer server supports two profiles, `sample` and `dev`, to run in various environments.

Each profile is designed to apply configurations suitable for the corresponding environment. By default, the Issuer server is configured with the `sample` profile, which is designed to run the server independently without integration with external services such as databases or blockchains. The `sample` profile is suitable for API call testing, allowing developers to quickly verify the basic operation of the application. This profile returns fixed response data for all API calls, making it useful in initial development environments.

Sample API calls are written as JUnit tests, so you can refer to them when writing tests.

In contrast, the `dev` profile is designed to perform actual operations. Using this profile enables testing and validation with real data. When the `dev` profile is activated, it integrates with external services such as actual databases and blockchains, allowing you to test application behavior in real environments.

### 6.1.1. `sample` Profile
The `sample` profile is designed to run the server independently without integration with external services (DB, blockchain, etc.). This profile is suitable for API call testing and allows developers to quickly verify the basic operation of the application. It returns fixed response data for all API calls, making it useful for initial development stages or functional testing. Since no integration with external systems is required, it provides an environment where you can run and test the server independently.
> Note: When using the sample profile, the Admin Console does not work.

### 6.1.2. `dev` Profile
The `dev` profile includes configurations suitable for development environments and is used on development servers. To use this profile, configuration for the development environment's database and blockchain nodes is required.

## 6.2. Profile Configuration Methods
This section explains how to change profiles for each execution method.

### 6.2.1. When Running Server Using IDE
- **Select Configuration File:** Choose the `application.yml` file from the `src/main/resources` path.
- **Specify Profile:** Add the `--spring.profiles.active={profile}` option in the IDE's run configuration (Run/Debug Configurations) to activate the desired profile.
- **Apply Configuration:** The corresponding configuration file is applied according to the activated profile.

### 6.2.2. When Running Server Using Console Commands
- **Select Configuration File:** Prepare profile-specific configuration files in the same directory as the built JAR file or in the path where configuration files are located.
- **Specify Profile:** Add the `--spring.profiles.active={profile}` option to the server startup command to activate the desired profile.
 
 ```bash
 java -jar build/libs/your-app-name.jar --spring.profiles.active={profile}
 ```

- **Apply Configuration:** The corresponding configuration file is applied according to the activated profile.

### 6.2.3. When Running Server Using Docker
- **Select Configuration File:** When creating Docker images, specify the configuration file path in the Dockerfile, or mount external configuration files to the Docker container.
- **Specify Profile:** Set the `SPRING_PROFILES_ACTIVE` environment variable in the Docker Compose file or Docker run command to specify the profile.
 
 ```yaml
 environment:
   - SPRING_PROFILES_ACTIVE={profile}
 ```

- **Apply Configuration:** Configuration is applied according to the specified profile when running the Docker container.

You can flexibly change profile-specific configurations according to each method, and easily apply configurations suitable for your project environment.


# 7. Running After Building with Docker

## 7.1. Docker Image Build Method (Based on `Dockerfile`)

### 7.1.1. Build Docker image
Build the Docker image with the following command:

```bash
cd {source_directory}
docker build -t did-issuer-server -f did-issuer-server/Dockerfile .
```

<br/>

## 7.2. Running with Docker Compose

### 7.2.1. Preparing Directories and Configuration Files

#### 1. Create docker-compose directory and config directory
```bash
mkdir -p {docker_compose_directory}/config
```

#### 2. Copy configuration files (yml) to config directory
```bash
cp {application_yml_directory}/* {docker_compose_directory}/config/
cp {blockchain_properties_path} {docker_compose_directory}/config/
```

#### 3. Modify blockchain.properties file
```yml
evm.network.url=http://host.docker.internal:8545
... (omitted)
```

> **host.docker.internal** is a special address that points to the host machine from within a Docker container.  
> Since localhost inside a container refers to the container itself, you must use host.docker.internal to access services (PostgreSQL, blockchain) running on the host.

#### 4. Modify application-database.yml file
```yml
spring:
 ... (omitted)
 datasource:
   driver-class-name: org.postgresql.Driver
   url: jdbc:postgresql://host.docker.internal:5430/issuer
   username: omn
   password: omn
 ... (omitted)
```

### 7.2.2. Create `docker-compose.yml` file
You can easily manage multiple containers using the `docker-compose.yml` file.

```yml
version: '3'
services:
 app:
   image: did-issuer-server
   ports:
     - "8091:8091"
   volumes:
     - {config_directory}:/app/config
   environment:
     - SPRING_PROFILES_ACTIVE=dev
   extra_hosts:
     - "host.docker.internal:host-gateway"
```

> - In the example above, the `config_directory` is mounted to `/app/config` inside the container to share configuration files.
>   - Configuration files located in `config_directory` take priority over default configuration files.
>   - For detailed configuration instructions, please refer to [5. Configuration Guide](#5-configuration-guide).


### 7.2.3. Run Container
```bash
cd {docker_compose_directory}
docker-compose up -d
```

<br/>

# 8. Installing Docker PostgreSQL

This section explains how to install PostgreSQL using Docker. Through this method, you can easily install PostgreSQL and integrate it with the server for use.

## 8.1. PostgreSQL Installation Using Docker Compose

Here's how to install PostgreSQL using Docker Compose.

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

This Docker Compose file installs PostgreSQL version 16.4 and configures the following settings:

- **container_name**: Sets the container name to `postgre-issuer`.
- **volumes**: Mounts the `postgres_data_issuer` volume to PostgreSQL's data directory (`/var/lib/postgresql/data`). This ensures data is permanently preserved.
- **ports**: Maps host port 5431 to container port 5432.
- **environment**: Sets PostgreSQL username, password, and database name. Here, `${USER}` and `${PW}` can be set as environment variables.

## 8.2. Running PostgreSQL Container

To run the PostgreSQL container using the above Docker Compose file, execute the following command in the terminal:

```bash
docker-compose up -d
```

This command runs the PostgreSQL container in the background. The PostgreSQL server runs according to the configured environment variables, and the database is prepared. You can proceed with integration configuration to use this database in your application.

<!-- References -->
[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md
[DID Besu Contract]: https://github.com/OmniOneID/did-besu-contract
[Open DID Admin Console Guide]: ../admin/OpenDID_IssuerAdmin_Operation_Guide_ko.md
