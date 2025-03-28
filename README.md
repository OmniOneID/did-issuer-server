Issuer Server
==

Welcome to the Issuer Server Repository. <br>
This repository contains the source code, documentation, and related resources for the Issuer Server.

## Folder Structure
Overview of the major folders and documents in the project directory:

```
did-issuer-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── dependencies-license.md
├── MAINTAINERS.md
├── README.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   └── admin
│       └── OpenDID_IssuerAdmin_Operation_Guide_ko.md
│   └── api
│       └── Issuer_API_ko.md
│   └── db
│       └── OpenDID_TableDefinition_Issuer.md
│   └── errorCode
│       └── Issuer_ErrorCode.md
│   └── installation
│       └── OpenDID_IssuerServer_Installation_Guide.md
└── source
    └── did-issuer-admin
        ├── frontend
    └── did-issuer-server
```

<br/>

Below is a description of each folder and file in the directory:

| Name                         | Description                                     |
| ---------------------------- | ----------------------------------------------- |
| CHANGELOG.md                 | Version-specific changes in the project         |
| CODE_OF_CONDUCT.md           | Code of conduct for contributors                |
| CONTRIBUTING.md              | Contribution guidelines and procedures          |
| LICENSE                      | License                                         |
| dependencies-license.md      | Licenses for the project’s dependency libraries |
| MAINTAINERS.md               | Guidelines for project maintainers              |
| RELEASE-PROCESS.md           | Procedures for releasing new versions           |
| SECURITY.md                  | Security policies and vulnerability reporting   |
| docs                         | Documentation                                   |
| ┖ admin                      | Admin Console guide documentation               |
| ┖ api                        | API guide documentation                         |
| ┖ db                         | Database Table Specifications                   |
| ┖ errorCode                  | Error codes and troubleshooting guides          |
| ┖ installation               | Installation and setup instructions             |
| source                       | Source code for the server and admin console    |
| ┖ did-issuer-server          | Issuer Server source code                       |
| ┖ did-issuer-admin           | Issuer Admin source code                        |
| &nbsp;&nbsp;&nbsp;┖ frontend | Issuer Admin frontend source code               |

<br/>

## Installation And Operation Guide

For detailed instructions on installing the Issuer Server, please refer to the guide below:
- [OpenDID Issuer Server Installation and Operation Guide](docs/installation/OpenDID_IssuerServer_Installation_Guide.md)  

For detailed instructions on operating the Issuer Admin Console, please refer to the guide below:  
- [OpenDID Issuer Admin Console Operation Guide](docs/admin/OpenDID_IssuerAdmin_Operation_Guide_ko.md)

## API Reference

API documentation is available in two main types:

- **Issuer API**: Detailed reference for the Issuer Server's API endpoints and usage.
  - [Issuer API Reference](docs/api/Issuer_API_ko.md)

## Change Log

The Change Log provides a detailed record of version-specific changes and updates. You can find it here:
- [Change Log](./CHANGELOG.md)
 
## OpenDID Demonstration Videos <br>
To watch our demonstration videos of the OpenDID system in action, please visit our [Demo Repository](https://github.com/OmniOneID/did-demo-server). <br>

These videos showcase key features including user registration, VC issuance, and VP submission processes.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License
[Apache 2.0](LICENSE)
