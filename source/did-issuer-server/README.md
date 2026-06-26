# Issuer Server Source Code

Welcome to the Issuer Server source code repository. This directory contains the core source code and build configurations for the Issuer Server.

## Directory Structure

Here's an overview of the directory structure.

```
did-issuer-server
├── gradle
├── libs
├── metadata
├── src
├── build.gradle
└── README.md
```

<br/>

Below is a description of each folder and file in the directory:

| Name                    | Description                                     |
| ----------------------- | ----------------------------------------------- |
| did-issuer-server       | Issuer Server source code and build files       |
| ┖ gradle                | Gradle build configurations and scripts         |
| ┖ libs                  | External libraries and dependencies             |
| ┖ metadata              | OID4VCI Issuer Metadata files                   |
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
    - `did-datamodel-sdk-server-2.0.0.jar`
    - `did-wallet-sdk-server-2.0.0.jar`
    - `did-zkp-sdk-server-2.0.0.jar`
    - `did-oid4vci-sdk-server-3.0.0.jar`
    - `did-oid4vc-authorization-sdk-server-3.0.0.jar`
    - `did-oid4vc-formatter-sdk-server-3.0.0.jar`
    - `did-sd-jwt-vc-sdk-server-3.0.0.jar`
    - `did-mso-mdoc-sdk-server-3.0.0.jar`

2. **Third-Party Libraries**: These libraries are managed via [build.gradle](build.gradle). Their licenses are listed in [dependencies-license.md](../../dependencies-license.md).


## Documentation

Refer to the following documents for more detailed information:

- [API Reference](../../docs/api/Issuer_API.md)  
  Detailed reference for the Issuer Server's API endpoints.

- [OpenDID Issuer Server Installation Guide](../../docs/installation/OpenDID_IssuerServer_Installation_Guide.md)
  Installation and configuration instructions.

- [Issuer Server Error Codes](../../docs/errorCode/Issuer_ErrorCode.md)

- [Issuer Database Table Definition](../../docs/db/OpenDID_TableDefinition_Issuer.md)

## Contributing

Please read [CONTRIBUTING.md](../../CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](../../CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
This project is licensed under the Apache License 2.0.

## Contact
For questions or support, please contact [maintainers](../../MAINTAINERS.md).
