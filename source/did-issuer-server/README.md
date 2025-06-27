# Issuer Server Source Code

Welcome to the Issuer Server source code repository. This directory contains the core source code and build configurations for the Issuer Server.

## Directory Structure

Here's an overview of the directory structure.

```
issuer
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
```

<br/>

Below is a description of each folder and file in the directory:

| Name                    | Description                                     |
| ----------------------- | ----------------------------------------------- |
| issuer                  | Issuer Server source code and build files       |
| ┖ gradle                | Gradle build configurations and scripts         |
| ┖ libs                  | External libraries and dependencies             |
| ┖ sample                | Sample files                                    |
| ┖ src                   | Main source code directory                      |
| ┖ build.gradle          | Gradle build configuration file                 |
| ┖ README.md             | Overview and instructions for the source code   |


## Libraries

Libraries used in this project are organized into two main categories:

1. **Open DID Libraries**: These libraries are developed by the Open DID project and are available in the [libs folder](libs). They include:

    - `did-sdk-common-2.0.0.jar`
    - `did-blockchain-sdk-server-2.0.0.jar`
    - `did-core-sdk-server-2.0.0.jar`
    - `did-crypto-sdk-server-2.0.0.jar`
    - `did-datamodel-server-2.0.0.jar`
    - `did-wallet-sdk-server-2.0.0.jar`
    - `did-zkp-sdk-server-2.0.0.jar`

2. **Third-Party Libraries**: These libraries are open-source dependencies managed via the [build.gradle](build.gradle) file. For a detailed list of third-party libraries and their licenses, please refer to the [LICENSE-dependencies.md](../../LICENSE-dependencies.md) file.


## Documenttation

Refer to the following documents for more detailed information:

- [API Reference](../../docs/api/Issuer_API.md)  
  Detailed reference for the Issuer Server's API endpoints.

- [OpenDID Issuer Server Installation and Operation Guide](../../docs/installation/OpenDID_IssuerServer_InstallationAndOperation_Guide.md)  
  Installation and configuration instructions.

## Contributing

Please read [CONTRIBUTING.md](../../CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](../../CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
This project is licensed under the Apache License 2.0.

## Contact
For questions or support, please contact [maintainers](../../MAINTAINERS.md).