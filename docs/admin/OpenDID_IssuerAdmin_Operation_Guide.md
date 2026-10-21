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

- Date: 2026-10-21
- Version: v2.0.1

Revision History
==
| Version | Date       | Changes                    |
| ------ | ---------- | -------------------------- |
| v1.0.0 | 2025-03-31 | Initial version                  |
| v1.0.1 | 2025-04-25 | Added `3.1. Issuer Registration` chapter <br> Modified `3.5. Issued VC Management` chapter |
| v2.0.0 | 2025-05-29 |  Added `3.4. ZKP Management` chapter |
| v2.0.0 | 2026-06-19 | Added OID4VCI, Server Configuration, and Password Policy sections |
| v2.0.1 | 2026-10-21 | Reorganized Chapter 3 to match the new side menu (OID4VCI / OpenDID / Issued Credentials) <br> Added `3.7.1. OID4VCI Credentials` (issuance history and status change) <br> Documented Issuance Mode and User Query Method of Issuance Profiles <br> Added the Credential Revocation Endpoint to Issuer Metadata <br> Documented the first-login ID and password change and refreshed all screenshots |

Table of Contents
==

- [OpenDID IssuerAdmin Operation Guide](#opendid-issueradmin-operation-guide)
- [Revision History](#revision-history)
- [Table of Contents](#table-of-contents)
- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. Admin Console Definition](#12-admin-console-definition)
- [2. Basic Manual](#2-basic-manual)
  - [2.1. Login](#21-login)
  - [2.2. Main Screen Configuration](#22-main-screen-configuration)
  - [2.3. Menu Configuration](#23-menu-configuration)
    - [2.3.1. Issuer Unregistered State](#231-issuer-unregistered-state)
    - [2.3.2. Issuer Registered State](#232-issuer-registered-state)
  - [2.4. Password Change Management](#24-password-change-management)
- [3. Function-specific Detailed Manual](#3-function-specific-detailed-manual)
  - [3.1. Issuer Registration](#31-issuer-registration)
  - [3.2. Issuer Management](#32-issuer-management)
  - [3.3. OID4VCI](#33-oid4vci)
    - [3.3.1. Issuer Metadata](#331-issuer-metadata)
    - [3.3.2. Credential Configurations](#332-credential-configurations)
    - [3.3.3. Client Settings](#333-client-settings)
    - [3.3.4. List Provider](#334-list-provider)
  - [3.4. OpenDID - VC](#34-opendid---vc)
    - [3.4.1. Namespaces](#341-namespaces)
    - [3.4.2. VC Schemas](#342-vc-schemas)
    - [3.4.3. Issuance Profiles](#343-issuance-profiles)
  - [3.5. OpenDID - ZKP](#35-opendid---zkp)
    - [3.5.1. Namespaces](#351-namespaces)
    - [3.5.2. Credential Schemas](#352-credential-schemas)
    - [3.5.3. Credential Definitions](#353-credential-definitions)
  - [3.6. User Management](#36-user-management)
  - [3.7. Issued Credentials](#37-issued-credentials)
    - [3.7.1. OID4VCI Credentials](#371-oid4vci-credentials)
    - [3.7.2. OpenDID Credentials](#372-opendid-credentials)
  - [3.8. Server Configuration](#38-server-configuration)
  - [3.9. Admin Management](#39-admin-management)
    - [3.9.1. Admin Management](#391-admin-management)
    - [3.9.2. Password Policy Settings](#392-password-policy-settings)

---

# 1. Introduction

## 1.1. Overview

This document provides guidance on the installation and operation of the Open DID Issuer Admin Console.
It is structured to help users efficiently utilize the console through step-by-step explanations from basic usage to detailed manuals for each function.

For a complete installation guide for OpenDID, please refer to the [Open DID Installation Guide].

## 1.2. Admin Console Definition

**Issuer Admin Console** is a web-based administrative tool for managing **Issuer servers** within the Open DID system.

The Issuer serves as the issuing entity for Verifiable Credentials (VCs), providing management functions for namespaces, schemas, and issuance policies (profiles) related to VC issuance. It also manages the settings for issuing SD-JWT VCs and mdocs over OID4VCI (OpenID for Verifiable Credential Issuance).

The main items that can be configured in the Issuer Admin Console are as follows:

- **Issuer Basic Information Management**
  - Issuer server registration and status verification
- **OID4VCI Issuance Settings**
  - Issuer Metadata (endpoints and encryption capabilities)
  - Credential Configuration registration and management
  - OID4VCI client settings and List Provider registration
- **OpenDID VC Management**
  - Namespace registration and item configuration
  - VC Schema registration and management
  - Issuance policy (Issuance Profile) configuration
- **OpenDID ZKP Management**
  - ZKP Namespace registration and item configuration
  - Credential Schema registration and management
  - Credential Definition registration and management
- **User and Issuance History Management**
  - Management of users eligible for issuance
  - Viewing and status management of credentials issued over OID4VCI and OpenDID
- **Server and Administrator Settings**
  - API keys for external server integration
  - Administrator accounts and password policy

# 2. Basic Manual

This chapter provides guidance on the basic usage of the Open DID Issuer Admin Console.

## 2.1. Login

To access the Issuer Admin Console, follow these steps:

1. Open a web browser and access the Issuer Admin Console URL.

   ```
   http://<issuer_domain>:<port>
   ```

2. On the login screen, enter the administrator account email and password.
   - Default administrator account: <admin@opendid.omnione.net>
   - Initial password: password

3. Click the 'SIGN IN' button.

Depending on the login result, one of the following steps follows.

| Situation | Behavior |
| --------- | -------- |
| First login of the ROOT account | The **Change ID and Password** dialog appears. You must replace the default account ID and initial password before entering the console. After the change, you are signed in with the new ID automatically. |
| Password reset required | After the first login of a newly registered administrator, after the password expires, or after a ROOT administrator changes the password, the **Password Reset** dialog appears. Save a new password to sign in automatically. |
| Otherwise | You are taken to `Issuer Registration` or `Issuer Management` depending on the Issuer registration state. |

> **Note:**  
> For security reasons, the default account information (ID and password) must be changed on the first login.  
> Password rules and expiration are configured in [3.9.2. Password Policy Settings](#392-password-policy-settings).

<br/>


## 2.2. Main Screen Configuration

The main screen displayed after login consists of the following elements:

<img src="./images/2-1.main-screen.png" width="600"/>

| Number | Area             | Description                                                                                                                                |
| ---- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| 1    | Header Area        | You can navigate to the password change screen through the `SETTING` button in the upper left corner.                                                        |
| 2    | Content Area      | The title of the currently selected menu and its corresponding content are displayed. The screen content changes according to each menu.                                            |
| 3    | Side Menu      | Located on the left side of the screen, with main menu items arranged vertically. The selected menu is highlighted, and sub-menus expand when necessary. |
| 4    | User Information Area | The email address of the currently logged-in administrator and the 'Sign Out' button are displayed.                                                        |

> **Note:** Every list screen provides a search key selector, a keyword field, a `SEARCH` button, and a refresh button that reloads the list.

<br/>

## 2.3. Menu Configuration

The sidebar menu of the Issuer Admin Console **differs in screen configuration depending on the Issuer registration status**.


<br/>

### 2.3.1. Issuer Unregistered State

In the initial state where the Issuer server has not yet been registered,
only the Issuer Registration item is displayed alone in the menu.

<img src="./images/2-3-1.issuer-menu-before-registration.png" width="250"/>

> Note: Once Issuer registration is completed, related functions are activated and the full menu expands.
Details about the menu configuration after registration will be explained in subsequent sections.

### 2.3.2. Issuer Registered State

Once Issuer registration is completed, all management functions are activated, and the sidebar menu is organized by protocol (OID4VCI / OpenDID) and management purpose as follows:

<img src="./images/2-3-2.issuer-menu-after-registration.png" width="260"/>

| Number | Menu Name | Depth | Description |
|------|-----------|--------|------|
| 1 | **Issuer Management** | 1 | Menu to verify and manage basic information (DID, status, etc.) of the Issuer server. |
| 2 | **OID4VCI** | 1 | Top-level menu for OID4VCI issuance settings. |
| 3 | └ Issuer Metadata | 2 | Manages the endpoints and encryption capabilities published in the Issuer Metadata. |
| 4 | └ Credential Configurations | 2 | Manages the credential configurations issued over OID4VCI. |
| 5 | └ Client Settings | 2 | Manages the common and platform-specific client settings used for OID4VCI issuance. |
| 6 | └ List Provider | 2 | Requests Issuer registration with the List Provider for user-initiated webview issuance. |
| 7 | **OpenDID** | 1 | Top-level menu for VC and ZKP issuance items of the OpenDID protocol. |
| 8 | └ VC | 2 | Menu group for standard VC issuance items. |
| 9 | &nbsp;&nbsp;&nbsp;└ Namespaces | 3 | Registers and manages namespaces used for VC issuance. |
| 10 | &nbsp;&nbsp;&nbsp;└ VC Schemas | 3 | Registers and manages VC schemas used for VC issuance. |
| 11 | &nbsp;&nbsp;&nbsp;└ Issuance Profiles | 3 | Manages the issuance profiles required for VC issuance. |
| 12 | └ ZKP | 2 | Menu group for ZKP issuance items. |
| 13 | &nbsp;&nbsp;&nbsp;└ Namespaces | 3 | Registers and manages ZKP namespaces used in Credential Schemas. |
| 14 | &nbsp;&nbsp;&nbsp;└ Credential Schemas | 3 | Registers and manages Credential Schemas used in Credential Definitions. |
| 15 | &nbsp;&nbsp;&nbsp;└ Credential Definitions | 3 | Registers and manages Credential Definitions used for ZKP credential issuance. |
| 16 | **User Management** | 1 | Menu to manage user information for VC recipients. |
| 17 | **Issued Credentials** | 1 | Top-level menu for viewing issued credentials by protocol. |
| 18 | └ OID4VCI Credentials | 2 | Views the issuance history of OID4VCI credentials and changes their status. |
| 19 | └ OpenDID Credentials | 2 | Views VCs issued over the OpenDID protocol and changes their status. |
| 20 | **Server Configuration** | 1 | Sets API keys used for external server integration. |
| 21 | **Admin Management** | 1 | Top-level menu for administrator accounts and password policy. |
| 22 | └ Admin Management | 2 | Views, registers, and deletes administrator accounts. |
| 23 | └ Password Policy Settings | 2 | Configures administrator password rules and expiration. |

> **Note:**  
> List screens use the new menu paths (`/opendid/vc/...`, `/opendid/zkp/...`, `/issued-credentials/...`), while registration, detail, and edit screens keep their existing paths (`/vc-management/...`, `/zkp-management/...`).  
> Detailed usage instructions for each function in the above menu configuration are explained in menu order in [Chapter 3. Function-specific Detailed Manual](#3-function-specific-detailed-manual).
<br/>

## 2.4. Password Change Management

User password changes can be performed through the following steps:

1. Click the 'SETTING' button in the header area.
2. Select 'Change Password' from the settings menu.
3. On the password change screen:
   - Enter current password
   - Enter new password
   - Re-enter new password for confirmation
4. Click the 'Save' button to apply the changes.

> **Note**: The password must satisfy the length and character rules (uppercase letters, numbers, special characters) configured in [3.9.2. Password Policy Settings](#392-password-policy-settings).

<br/>

# 3. Function-specific Detailed Manual

This chapter describes the main functions of the Issuer Admin Console in side menu order.

> **Important Notice**
> The main functions of the Issuer Admin Console—including all VC issuance-related configurations—can only be used **after completing Issuer Registration**.
> The VC management menus within the system are enabled only after the Issuer is registered, and VC issuance configurations become available at that point.

To actually issue VCs, the following preparatory steps must be completed first:

* **Basic Setup for OID4VCI Issuance**

  1. **Check Issuer Metadata** – Verify the Credential Issuer URL, Authorization Servers, and endpoints
  2. **Register Credential Configurations** – Define the format, identifiers, and metadata of each credential
  3. **Check Client Settings** – Configure wallet and authorization clients
  4. **(For user-initiated webview issuance) Request List Provider registration**

* **Basic Setup for OpenDID VC Issuance**

  1. **Register Namespace** – Define the claim fields to be included in the VC
  2. **Register VC Schema** – Define the VC data structure based on the Namespace
  3. **Register Issuance Profile** – Configure the issuance method and policy

* **Additional Setup for ZKP-based VC Issuance**

  1. **Register ZKP Namespace** – Define ZKP-specific claim structure
  2. **Register Credential Schema** – Define VC schema optimized for ZKP
  3. **Register Credential Definition** – Define a credential structure linked to the ZKP network
  4. **Include ZKP Settings in Issuance Profile** – Enable ZKP issuance by linking the relevant Definition ID

Only after completing the above steps can users be registered and VCs be issued.
Attempting to proceed out of order may result in issuance failure or disabled UI elements.

> **Note**
> All VC-related configurations must be performed **after Issuer Registration**.
> The system dynamically enables UI elements or displays error messages based on whether prerequisite items exist.

## 3.1. Issuer Registration

Issuer Registration is the procedure for registering an Issuer as the issuing entity through the Issuer Admin Console. The complete registration consists of 3 steps, with input, request, and approval processes for each step to complete registration.
Once all steps are completed, the Issuer is officially registered in the OpenDID network.

<br/>

###  ▸ Step 1 – Enter Issuer Info

<img src="./images/3-1-1.issuer-registration-step1.png" width="700"/>

This is the step to enter the Issuer's name and server URL.

- **Name**: Name of the Issuer to register  
- **Issuer URL**: Server endpoint in the format `http://{IP}:8091/issuer`  

> **Note**  
> The Issuer server and Admin Console use the same Base URL and are separated as packages within the same application.

- After entering all items, click the **NEXT** button to move to the next step.

<br/>

### ▸ Step 2 – Register DID Document

This is the step to create the Issuer's DID Document and receive registration request and approval from TAS (Trust Agent Service). Follow the order below:

<br/>

**1. Generate DID Document**  
<img src="./images/3-1-1.issuer-registration-step2-1.png" width="500"/>  
Clicking the `GENERATE` button automatically creates a DID document and displays it in JSON format on the screen.  
> Completion message: ✅ DID Document has been successfully created.

<br/>

**2. Submit Registration Request**  
<img src="./images/3-1-1.issuer-registration-step2-2.png" width="500"/>  
Clicking the `REQUEST` button sends a DID document registration request to TAS.  
> Request completion message: ✅ Registration request has been submitted.

<br/>

**3. Check Approval Status**  
<img src="./images/3-1-1.issuer-registration-step2-3.png" width="500"/>  
Click the `CHECK` button to verify approval from the TAS administrator.  
> Approval completion message: ✅ Approval confirmed. You can proceed.

<br/>

**4. Screen after completing all processes**  
<img src="./images/3-1-1.issuer-registration-step2-4.png" width="500"/>

<br/>

### ▸ Step 3 – Register Entity and Issue Certificate VC

<img src="./images/3-1-1.issuer-registration-step3.png" width="700"/>

This is the final step to register the Issuer as an entity in the OpenDID network and receive a **Certificate VC (trust verification VC)**.

- Clicking the `REQUEST` button registers the Entity through TAS.
- Once registration is completed, click the `FINISH` button to complete the entire registration.
- When TAS returns the public URL of the Certificate VC in the entity enrollment response, it is stored with the Issuer information and shown as the **Certificate URL** in [3.2. Issuer Management](#32-issuer-management).

> **Note**  
> Certificate VC is a credential that proves trust relationships between OpenDID components.

<br/>

### ▸ Registration Completion Screen

Once all steps are successfully completed, the completion screen is displayed as shown below.

<img src="./images/3-1-1.issuer-registration-completed.png" width="700"/>

- A "Completed" message appears, and you can click the `GO TO HOME` button to navigate to the Admin Console main screen.


## 3.2. Issuer Management

Issuer Management is a menu for registering and managing basic information of the Issuer server. The Issuer must be registered with a unique DID in the system as the issuing entity for VCs (Verifiable Credentials), and registration is required only once initially.  

Once the Issuer is registered, it is displayed as active status (`ACTIVATE`) in the system, and after registration, you can view the registered information or make changes within a limited scope.

The Issuer Management screen consists of the following items.

<img src="./images/3-2-1.issuer-management-info.png" width="600"/>

| Item              | Description                                                           |
|-------------------|----------------------------------------------------------------|
| **DID**           | Unique DID that identifies the Issuer, automatically issued by the system.     |
| **Name**          | Name of the Issuer to register.                                    |
| **Status**        | Indicates the status of the Issuer. Displayed as `ACTIVATE` when registration is completed. |
| **URL**           | Base URL of the Issuer server.                                  |
| **Certificate URL** | URL where the Issuer's certificate of registration (Certificate VC) can be retrieved. |
| **Registered At** | Initial registration date and time of the Issuer.                                 |

- The DID Document can be verified through the `VIEW DID DOCUMENT` button on the screen.
- Click the `VIEW` button next to the Certificate URL to view the Certificate VC.
- Registered Issuer information cannot be deleted and can only be modified within a limited scope for certain items.

## 3.3. OID4VCI

The OID4VCI menu manages the settings for issuing SD-JWT VCs (`dc+sd-jwt-did`) and mdocs (`mso_mdoc-did`) over OID4VCI (OpenID for Verifiable Credential Issuance). It appears after the Issuer is activated and contains four sub-menus.

| Menu | Route | Managed Data |
|------|-------|--------------|
| Issuer Metadata | `/oid4vci-management/metadata` | Issuer URL, Authorization Servers, Offer/Credential/Nonce/Revocation/Deferred/Notification endpoints, and encryption capabilities |
| Credential Configurations | `/oid4vci-management/credential-config` | Configuration ID, format, credential identifiers, and metadata JSON |
| Client Settings | `/oid4vci-management/property` | Client settings for common, Android, iOS, Android OpenID, and iOS OpenID platforms |
| List Provider | `/oid4vci-management/list-provider-registration` | List Provider URL and registration state used for user-initiated webview issuance |

Saving these screens calls `/issuer/admin/v1/oid4vci/metadata`, `/credential-configs`, `/properties`, or `/list-provider-registration`. Endpoint changes affect the public metadata and dynamically registered issuance endpoints, so validate all URLs before saving.

### 3.3.1. Issuer Metadata

This screen displays and updates the endpoints published in the OID4VCI Issuer Metadata (`/.well-known/openid-credential-issuer`) and the supported encryption settings for credential requests and responses.

<img src="./images/3-3-1.oid4vci-issuer-metadata.png" width="700"/>

| Number | Item | Description |
| ------ | ---- | ----------- |
| 1 | **EDIT Button** | Switches the Issuer Metadata screen to edit mode. |
| 2 | **Credential Request Encryption** | Configures the supported content-encryption methods and whether encryption is required for credential requests. |
| 3 | **Credential Response Encryption** | Configures the supported key- and content-encryption methods and whether encryption is required for credential responses. |
| 4 | **Credential Issuer (Main Endpoint)** | Base URL of the OID4VCI Credential Issuer. |
| 5 | **Authorization Servers** | List of OIDC Authorization Server URLs that handle authentication and authorization. |
| 6 | **Credential Offer Endpoint** | Endpoint that provides credential offers. |
| 7 | **Credential Endpoint** | Endpoint that processes credential issuance requests. |
| 8 | **Nonce Endpoint** | Endpoint used to request a new `c_nonce`. Each `c_nonce` can be used only once. |
| 9 | **Credential Revocation Endpoint** | Endpoint that a wallet uses to request revocation of a credential it holds. Always `{Credential Issuer}/credential-lifecycle/revoke`. |
| 10 | **Deferred Credential Endpoint** | Endpoint that processes deferred credential requests. |
| 11 | **Notification Endpoint** | Endpoint that processes credential issuance result notifications. |

> **Note:**  
> - In edit mode, click **SAVE** to save the changes or **CANCEL** to discard them.  
> - The Offer, Credential, Nonce, Revocation, Deferred, and Notification endpoints are fixed values and cannot be edited.  
> - For the request format of the Credential Revocation Endpoint, see the [Issuer API document](../api/Issuer_API.md).  
> - `credential_configurations_supported` in the public metadata is built from the configurations registered in [3.3.2. Credential Configurations](#332-credential-configurations).

---

### 3.3.2. Credential Configurations

This screen displays, registers, updates, and deletes credential configurations available for OID4VCI issuance.

<img src="./images/3-3-2-1.oid4vci-credential-config.png" width="700"/>

| Number | Item | Description |
| ------ | ---- | ----------- |
| 1 | **REGISTER Button** | Opens the screen for registering a new credential configuration. |
| 2 | **UPDATE Button** | Opens the detail screen for the selected credential configuration so that it can be updated. |
| 3 | **DELETE Button** | Deletes the selected credential configuration. |
| 4 | **ID** | Unique identifier of the credential configuration. Click the ID to open its detail screen. |
| 5 | **Format** | Format of the credential to be issued. (`mso_mdoc-did`, `dc+sd-jwt-did`) |
| 6 | **Identifiers** | List of identifiers used by the SDK to identify the credential. |
| 7 | **Registered Date** | Date and time when the credential configuration was registered. |
| 8 | **Modified Date** | Date and time when the credential configuration was last modified. |

#### ▸ Credential Configuration Registration

<img src="./images/3-3-2-2.oid4vci-credential-config-registration.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **Configuration ID** | Unique identifier of the configuration. It can be entered only when registering. |
| **Format** | Select `mso_mdoc-did` or `dc+sd-jwt-did`. The default is `mso_mdoc-did`. |
| **Identifiers (SDK)** | Identifiers used by the SDK to identify the credential. Use the `ADD` button to register more than one. |
| **Metadata JSON** | JSON object published in `credential_configurations_supported` of the Issuer Metadata. |

- Click `REGISTER` to save, or `CANCEL` to return to the list.
- Metadata JSON must be a JSON object; arrays, strings, and other values cannot be saved.

#### ▸ Credential Configuration Detail / Update

<img src="./images/3-3-2-3.oid4vci-credential-config-detail.png" width="700"/>

- Click an ID in the list, or select a row and click `UPDATE`, to open the detail screen.
- The Configuration ID cannot be changed. Edit the Format, Identifiers, and Metadata JSON, then click `SAVE`.

> **Note:** Credential configurations are saved only to the credential configuration store. `credential_configurations_supported` in the public Issuer Metadata is built from the stored configurations when it is read, so you do not need to edit the Issuer Metadata when registering, updating, or deleting a configuration.

---

### 3.3.3. Client Settings

This screen manages the common and platform-specific client settings used during OID4VCI issuance.

<img src="./images/3-3-3.oid4vci-client-settings.png" width="700"/>

| Number | Item | Description |
| ------ | ---- | ----------- |
| 1 | **SAVE Button** | Saves the client settings entered on the screen. |
| 2 | **Main Client Settings** | Configures the common Client ID, Client Secret, Redirect URL, Redirect URIs, and scopes. |
| 3 | **Android Client** | Configures the Client ID, Redirect URIs, and scopes used by the Android client. |
| 4 | **iOS Client** | Configures the Client ID, Redirect URIs, and scopes used by the iOS client. |
| 5 | **Android OpenID Client** | Configures the Client ID, Redirect URIs, and scopes used by the Android OpenID client. |
| 6 | **iOS OpenID Client** | Configures the Client ID, Redirect URIs, and scopes used by the iOS OpenID client. |

---

### 3.3.4. List Provider

This screen registers Issuer information with the List Provider for user-initiated webview issuance and displays the registration status.

<img src="./images/3-3-4.oid4vci-list-provider.png" width="700"/>

| Number | Item | Description |
| ------ | ---- | ----------- |
| 1 | **Registration Status** | Displays the registration request status returned by the List Provider (`PENDING`, `ACTIVE`, `REJECTED`, `REVOKED`). It is not shown before a request is made. |
| 2 | **List Provider URL** | URL of the List Provider to which the registration request is sent. (Read-only) |
| 3 | **Credential Issuer** | Credential Issuer identifier loaded from the Issuer DID information. (Read-only) |
| 4 | **Credential Issuer Metadata URI** | URI from which the List Provider can retrieve the Issuer Metadata. Must be an HTTP(S) URL. |
| 5 | **User Initiation URI** | URI used by a user to start the webview issuance flow. Must be an HTTP(S) URL. |
| 6 | **REFRESH Button** | Reloads the List Provider registration information and current status. |
| 7 | **REQUEST REGISTRATION Button** | After a confirmation dialog, requests registration with the List Provider using the entered information. The inputs and button are disabled after the request. |

---

## 3.4. OpenDID - VC

The OpenDID > VC menu is used to define and manage the VCs (Verifiable Credentials) that the Issuer issues over the OpenDID protocol. To issue VCs, you must first register Namespaces and VC Schemas to define the VC structure and configure issuance policies (Issuance Profiles).

It consists of the following three sub-menus.

- Namespaces
- VC Schemas
- Issuance Profiles

---

### 3.4.1. Namespaces

Namespaces is the menu used to register and manage namespaces, which are used to define the structure of a VC.
A namespace is a **logical grouping of claim (attribute) items to be included in a single VC**, and each item includes information such as `ID`, `Type`, `Format`, and `Caption`.

Once registered, a namespace is selected as part of the configuration when creating a VC Schema, serving as the **foundation for constructing the data structure of the VC**. It is also indirectly referenced when creating an Issuance Profile,
making it a **core element that defines the input fields required during VC issuance**.

In summary, a namespace is used in the overall VC issuance system as follows:

1. **Register Namespace** – Define the items to be included in the VC
2. **Select Namespace when creating VC Schema** – Define VC structure
3. **Reference VC Schema when registering Issuance Profile**
4. **Use as the basis for entering VC data (JSON) during user registration**

> This menu is **available after Issuer registration**. Since it is referenced during VC Schema and Issuance Profile registration, it is a **required item to be configured first**.

#### ▸ Namespace List

In the Namespace list screen, you can verify registered namespaces.

<img src="./images/3-4-1-1.vc-namespace-management.png" width="700"/>

| Item                | Description                                                  |
| ------------------- | ------------------------------------------------------------ |
| **ID**              | Unique identifier of the namespace.                          |
| **Name**            | Name of the namespace. Click to navigate to the detail view. |
| **VC Schema Count** | Number of VC Schemas referencing this namespace.             |
| **Registered At**   | Date and time when the namespace was registered.             |

* Click the `REGISTER` button to create a new namespace.
* After selecting a namespace, click the `UPDATE` button to modify it.

  > You can modify the namespace only if no VC Schema is using it.
  > If it is referenced by a VC Schema, modification is not allowed.

#### ▸ Namespace Registration

In the namespace registration screen, you create a namespace by entering the following information.

<img src="./images/3-4-1-2.vc-namespace-registration.png" width="500"/>

| Item            | Description                                              |
|-----------------|---------------------------------------------------|
| **Namespace ID**| Unique identifier of the namespace (e.g., `iso.18013.5`).|
| **Name**        | Namespace name.                           |
| **Ref**         | Reference URL or description related to the namespace.      |
| **Items**       | Items to be included in the namespace. Consists of the following fields:<br>-`ID`, `Type`, `Format`, `Caption`|

- Add items with the `ADD ITEM` button, and you can also delete and modify items.
- After input, click the `REGISTER` button to register.

#### ▸ Namespace Detailed Information

Clicking the name in the namespace list allows you to verify detailed information.

<img src="./images/3-4-1-3.vc-namespace-detail.png" width="500"/>

| Item            | Description                             |
|-----------------|----------------------------------|
| **Namespace ID**| Unique identifier of the namespace.  |
| **Name**        | Namespace name.         |
| **Ref**         | Reference URL or description.        |
| **Items**       | List of registered items. Each item consists of ID, Type, Format, Caption.|


#### ▸ Namespace Update

This is a screen where you can modify existing namespace information.

<img src="./images/3-4-1-4.vc-namespace-update.png" width="500"/>

| Item             | Description                                   |
|------------------|----------------------------------------|
| **Namespace ID** | Cannot be modified as it is a unique identifier.  |
| **Name**         | You can modify the namespace name. |
| **Ref**          | You can modify the reference URL or description.|
| **Items**        | You can add, modify, and delete registered items.|

- After modifying changes, click the `UPDATE` button to save.
- The `RESET` button returns the form to its initial state, and you can move to the detailed view screen with the `BACK` button.

---

### 3.4.2. VC Schemas

VC Schemas is the menu for registering and managing **VC Schemas** that define the data structure of Verifiable Credentials (VC).

A VC Schema is a schema document that defines the **structure and format of the data fields (Claims)** to be included in a VC.
It is created by **combining claim items defined in pre-registered Namespaces**.
In other words, a VC Schema is based on a Namespace and **clearly specifies what information will be included in the VC**.

Once registered, the VC Schema is **referenced during Issuance Profile creation**,
and when entering user information for issuance, the VC JSON structure is determined based on this Schema.
Thus, the VC Schema **serves as the core reference point for VC issuance** and is used in the following flow:

1. **Register Namespace** – Define claim items
2. **Register VC Schema** – Define VC structure based on Namespace
3. **Select VC Schema when registering Issuance Profile**
4. **Enter JSON data based on Schema during user registration**
5. **Generate actual VC using the Schema at issuance time**

> This menu is **available only after Issuer registration and prior Namespace registration**.
> A VC Schema is also registered on the ledger (blockchain or Ledger Service Server) when it is registered, so the connected servers must be running.

#### ▸ VC Schema List

Verify the list of registered VC Schemas.

<img src="./images/3-4-2-1.vc-schema-management.png" width="700"/>

| Item            | Description                             |
|-----------------|----------------------------------|
| **ID**          | Unique identifier of the VC Schema.   |
| **Title**       | Title of the schema. Clicking moves to the detailed screen.|
| **Registered At**| Date and time of initial registration.          |
| **Updated At**  | Date and time of last modification.         |

- You can register a new VC Schema by clicking the `REGISTER` button.

#### ▸ VC Schema Registration

Register a new VC Schema.

<img src="./images/3-4-2-2.vc-schema-registration.png" width="700"/>

| Item            | Description                                        |
|-----------------|---------------------------------------------|
| **VC Schema ID**| Unique identifier or URL of the VC Schema.      |
| **Title**       | Title of the schema.                         |
| **Language**    | VC Schema language (e.g., `ko`).                   |
| **Version**     | Schema version.                           |
| **Description** | Description of the VC Schema.                 |
| **Items**       | List of namespace-based items to be included in the Schema.|

- After completing input, register with the `REGISTER` button.

#### ▸ VC Schema Detailed Information

Clicking the title in the VC Schema list allows you to verify detailed information of the schema.

<img src="./images/3-4-2-3.vc-schema-detail.png" width="700"/>

| Item                 | Description                                                    |
|----------------------|---------------------------------------------------------|
| **VC Schema ID**     | Unique URL or ID that identifies the VC Schema.          |
| **Title**            | Title of the VC Schema.                                 |
| **Language**         | Schema description language setting. (e.g., `ko`)                 |
| **Version**          | Version information of the schema.                               |
| **Description**      | Detailed description of the schema.                          |
| **Credential Subject** | List of Namespaces included in the VC Schema. Clicking an item allows you to verify detailed information of that Namespace. |

---

### 3.4.3. Issuance Profiles

Issuance Profiles is the menu for defining and managing **Issue Profiles** used for issuing Verifiable Credentials (VCs).

An Issue Profile is a **VC issuance policy** that bundles together the VC Schema, issuance method (e.g., User/Issuer Initiate), issuance path (Direct/Proxy), user information lookup method, security settings, and other parameters into a single unit.

To issue a VC, an Issue Profile based on the relevant VC Schema must be registered.
During issuance, the system references this profile to automatically configure the issuance process, structure, and security settings.

An Issue Profile consists of the following elements:

* VC Schema ID to reference
* VC issuance method (`User Initiate`, `Issuer Initiate`)
* Issuance path (`Direct`, `Proxy`)
* List of endpoint addresses used during issuance
* End-to-End (E2E) encryption settings
* User information lookup method (`DB`, `TEST`)
* (Optional) ZKP issuance option and Credential Definition ID

> **To issue ZKP Credential**, a ZKP Credential Definition must be registered in advance.
> The Definition ID must be linked during Issue Profile creation to enable ZKP Credential issuance.

#### Usage Flow

1. **Register Namespace** → Define claim items
2. **Register VC Schema** → Define structure
3. **Register Credential Definition** (for ZKP issuance)
4. **Register Issue Profile** → Define issuance policy
5. **Register user and issue VC**

> Multiple Issue Profiles can be defined for a single VC Schema,
> allowing different issuance methods and security settings depending on the intended use case (e.g., for general users vs testing).

> This menu is available **after completing Issuer registration, Namespace registration, and VC Schema registration**.
> To issue ZKP-based VCs, **Credential Definition registration must also be completed beforehand**.

#### Issuance Mode and User Query Method

| Item | Value | Description |
| ---- | ----- | ----------- |
| **Issuance Mode** | `Direct` | Default issuance path in which the wallet requests the VC directly from the Issuer. |
| | `Proxy` | Issuance path through a relay server. The issue-profile request carries a user ID instead of the holder, the Issuer retrieves the user's PII from the KYC server, and the holder's DID Auth signature is verified. |
| **User Query Method** | `DB` (default) | Uses the user information (VC claims) registered in advance in User Management. Existing Issue Profiles behave as `DB`. |
| | `TEST` | Issues a VC with test claims generated automatically from the VC Schema, without registering user information in advance. Use it only in development and verification environments. |

> **Note:** In the VC information popup of the demo server, the `Skip` link lets the user skip input. With `TEST`, issuance continues because claims are generated automatically; with `DB`, skipping leaves no user information and issuance may fail at a later step.

#### ▸ Issue Profile List

You can verify the list of registered Issue Profiles.

<img src="./images/3-4-3-1.issuance-profile-management.png" width="700"/>

| Item            | Description                                             |
|-----------------|--------------------------------------------------|
| **VC Plan ID**  | Unique ID that identifies the Issue Profile.|
| **Title**       | Title of the profile. Clicking moves to the detailed screen.|
| **VC Schema ID**| ID of the linked VC Schema.|
| **User Query Type** | User information lookup method (`DB`, `TEST`). |
| **Registered At**| Date and time when the Issue Profile was registered.               |

- You can register a new Issue Profile by clicking the `REGISTER` button.
- Select an item, then use `UPDATE` to open the edit screen or `DELETE` to delete it.

#### ▸ Issue Profile Registration

<img src="./images/3-4-3-2.issuance-profile-registration.png" width="500"/>

| Item               | Description                                                                 |
|--------------------|----------------------------------------------------------------------|
| **VC Plan ID**     | Unique identifier of the issuance profile.                                   |
| **Title**          | Title of the issuance profile.                                           |
| **Description**    | Detailed description of the profile.                              |
| **VC Schema ID**   | ID of the VC Schema to link. (Selectable with magnifying glass button)              |
| **Initiate Type**  | Setting for how VC issuance requests are initiated<br/>(e.g., User Initiate, Issuer Initiate)|
| **Language**       | Language setting of the profile. (e.g., `ko`)                                |
| **Issuance Mode**  | Issuance path. (`Direct`, `Proxy`) |
| **Endpoints**      | List of Endpoint addresses to receive VC issuance requests.                       |
| **E2E**            | End-to-End encryption configuration<br/>- Cipher, Curve, Padding settings available          |
| **Tags**           | Tag information to distinguish or search for this profile.             |
| **User Query Method** | User information lookup method. (`DB`, `TEST`; default `DB`) |
| **ZKP Issuance** | For `Issuer Initiate` method, you can set whether to issue based on ZKP. |
| **Credential Definition ID** | Enter the Credential Definition ID to reference for ZKP issuance.     |

- Turning on the `ZKP Issuance` switch activates the Definition ID input field.
- After entering all items, the profile is registered when you click the `REGISTER` button.  
- The `RESET` button initializes the form, and `CANCEL` cancels registration.

#### ▸ Issue Profile Detailed Information

<img src="./images/3-4-3-3.issuance-profile-detail.png" width="500"/>

- You can verify detailed information of registered Issue Profiles.
- If `ZKP Issuance` is set, the related Definition ID is displayed at the bottom.

| Item                     | Description                                                  |
|--------------------------|-------------------------------------------------------|
| **VC Plan ID**           | Unique identifier of the issuance profile                          |
| **Title**                | Title of the profile                                       |
| **Description**          | Description of the profile                                 |
| **VC Schema ID**         | ID of the linked VC Schema                                 |
| **Initiate Type**        | Issuance initiation method (`Issuer Initiate`, `User Initiate`)   |
| **Language**             | Profile language                                         |
| **Issuance Mode**        | Issuance path (`Direct`, `Proxy`)                         |
| **Endpoints**            | List of Endpoints to receive issuance requests                      |
| **E2E**                  | Cipher, Curve, Padding settings                           |
| **Tags**                 | Tag information                                             |
| **User Query Method**    | User information lookup method (`DB`, `TEST`)             |
| **ZKP Definition ID**    | Credential Definition ID to be used for ZKP-based issuance       |

- You can move to the edit screen by clicking the `GO TO EDIT` button.

#### ▸ Issue Profile Update

<img src="./images/3-4-3-4.issuance-profile-edit.png" width="500"/>

- You can modify the content of registered Issue Profiles.
- The `ZKP Issuance` setting is displayed only when Initiate Type is `Issuer Initiate`.

| Item                     | Description                                                  |
|--------------------------|-------------------------------------------------------|
| **VC Plan ID**           | Cannot be modified.                                   |
| **Title**                | Profile title can be modified                             |
| **VC Schema ID**         | Already linked VC Schema cannot be modified                     |
| **Initiate Type**        | Issuance initiation method can be modified                              |
| **Language**             | Language settings can be modified                                   |
| **Issuance Mode**        | Issuance path can be modified                             |
| **Endpoints**            | Can be added and modified                                     |
| **E2E**                  | Cipher, Curve, Padding can be modified                      |
| **Tags**                 | Tags can be added/deleted                                   |
| **User Query Method**    | User information lookup method can be modified           |
| **ZKP Issuance**        | Can only be set when issuance method is Issuer Initiate           |
| **Credential Definition ID** | Specifies Definition ID used for ZKP issuance             |

- Save changes by clicking the `UPDATE` button.
- `RESET` initializes input values, and `BACK` moves to the detailed view screen.

---

## 3.5. OpenDID - ZKP

The OpenDID > ZKP menu provides functionality to register and manage components required for issuing **Zero-Knowledge Proof (ZKP)**-based Credentials.
ZKP allows **proof of specific information without directly disclosing the user's sensitive data**, enabling **more privacy-preserving VC issuance**.

To configure ZKP-based issuance, the following three registration steps must be completed in advance.
The data generated through these steps is referenced later during **Issuance Profile registration** under the ZKP issuance section:

1. **Register ZKP Namespace** – Define claims (attributes) to be included in the VC
2. **Register Credential Schema** – Define the ZKP VC structure based on the namespace
3. **Register Credential Definition** – Create a definition based on the schema for actual issuance
   → This definition generates a unique Definition ID to be used in the ZKP network

The ZKP menu supports these steps in order and is structured separately from general VC Schema management.

#### ▸ ZKP VC Issuance Flow Summary

1. **Register ZKP Namespace**
2. → **Register ZKP Credential Schema (based on Namespace)**
3. → **Register ZKP Credential Definition (based on Schema)**
4. → **Enable ZKP issuance option and link Definition ID when creating an Issuance Profile**
5. → **Register user and issue ZKP-based VC**

> To issue a ZKP-based VC, the `Initiate Type` of the Issue Profile must be set to `Issuer Initiate`,
> and the `ZKP Issuance` option must be enabled with a selected Definition ID.

> All ZKP menus are available **only after completing Issuer registration**,
> and failing to follow the registration order may cause errors in Issue Profile configuration or VC issuance.

### 3.5.1. Namespaces

ZKP Namespaces is the space for defining claim (attribute) items to be included in **Zero-Knowledge Proof (ZKP)**-based VCs.
Namespaces registered here serve as the foundational unit for ZKP Credential Schemas and clearly specify the **verifiable data items** that will be included in a ZKP VC.

A namespace consists of **one or more claim items** (such as Label, Type, Caption),
and each claim corresponds to the actual user information at the time of issuance and becomes the subject of **ZKP-based proof**.

ZKP Namespaces are later **referenced as claim lists during Credential Schema registration**,
and through the schema and definition, they are ultimately **linked to the ZKP Definition ID in the Issue Profile**.

Therefore, to configure ZKP-based VC issuance, you must first define the claim structure through this menu.
Namespaces can be modified after registration, but **deletion is restricted**.

> Example claim items:
>
> * Label: birth\_year
> * Type: Number
> * Caption: Birth Year

> This menu is **available after completing Issuer registration**. It serves as the starting point for defining claims used in ZKP-based VC issuance and must be referenced when registering Credential Schemas and Definitions.

#### ▸ ZKP Namespace List

- You can verify the list of previously registered ZKP Namespaces.
- Each item displays ID, Name, Schema Count, and registration/modification date and time.
- Clicking Name moves to the detailed screen.

<img src="./images/3-5-1-1.zkp-namespace-management.png" width="700"/>

| Item            | Description                                     |
|-----------------|------------------------------------------|
| **ID**          | Unique ID of the Namespace                      |
| **Name**        | Namespace name (clicking moves to detailed screen)  |
| **Schema Count**| Number of Schemas referencing this Namespace     |
| **Registered At**| Initial registration date and time                          |
| **Updated At**   | Last modification date and time                         |

- `REGISTER` button: Moves to new Namespace registration screen  
- When selecting items, `UPDATE`, `DELETE` buttons are activated

#### ▸ ZKP Namespace Registration

<img src="./images/3-5-1-2.zkp-namespace-registration.png" width="700"/>

| Item             | Description                                                              |
|------------------|-------------------------------------------------------------------|
| **Namespace ID** | Unique identifier (check for duplicates with the `Check Availability` button) |
| **Name**         | Namespace name                                                    |
| **Ref**          | Related description or reference link                                           |
| **Items**        | List of Claim items to include (Label, Type, Caption required; Type is `String` or `Number`) |

- You can add items with the `ADD ITEM` button.  
- Click the `REGISTER` button when registration is complete  
- `RESET` button initializes the form, `CANCEL` cancels registration

#### ▸ ZKP Namespace Detailed Information

<img src="./images/3-5-1-3.zkp-namespace-detail.png" width="700"/>

- You can view registered Namespace information.
- You can verify the list of Claim items in the Attributes table at the bottom.

#### ▸ ZKP Namespace Update

> You can modify the namespace if it is not used by any Credential Schema.
> If it is referenced by a Credential Schema, modification is not allowed.

<img src="./images/3-5-1-4.zkp-namespace-edit.png" width="700"/>

- Claim items can be added/deleted and content can be modified.
- Save with the `UPDATE` button after modification

---

### 3.5.2. Credential Schemas

Credential Schemas is the menu for defining and managing **ZKP Credential Schemas** used in ZKP-based Credential issuance.

A ZKP Credential Schema is **constructed based on the claim items defined in the ZKP Namespace**, and determines what information will be included in the VC.

This schema has a structure separate from the standard VC Schema and includes **only claim information optimized for ZKP**.
Each claim included in the schema is later used as an input for ZKP proof and is **linked one-to-one with a ZKP Definition**.

#### Differences Between Standard VC Schema and ZKP Credential Schema

| Item           | VC Schema (Standard VC)         | ZKP Credential Schema                  |
| -------------- | ------------------------------- | -------------------------------------- |
| Reference Base | Standard VC Namespace           | ZKP Namespace                          |
| Purpose        | Claim structure for VC issuance | VC structure for ZKP proof             |
| Usage          | Referenced by Issue Profile     | Linked to Credential Definition        |
| Key Features   | Can include various claims      | **Contains only claims for ZKP proof** |

The Credential Schema must be **selected when registering a Credential Definition**,
and through the Definition ID, it is ultimately linked to the Issue Profile.

When composing a Credential Schema, you must check **whether the selected claim items are suitable for ZKP proof**,
and assemble them based on the ZKP Namespace.

> This menu is **available only after completing Issuer registration and ZKP Namespace registration**.
> The Credential Schema defines the structure for ZKP issuance and becomes a core reference for generating ZKP Definitions and issuing VCs.

#### ▸ Credential Schema List

<img src="./images/3-5-2-1.credential-schema-management.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **ID** | Credential Schema ID registered on the ledger. (e.g., `did:omn:issuer:2:tec_student_zkp:1.0`) |
| **Name** | Schema name. Clicking moves to the detailed screen. |
| **Version** | Schema version. |
| **Definition Count** | Number of Credential Definitions referencing this schema. |
| **Status** | Ledger registration status. |
| **Registered At / Updated At** | Registration and modification date and time. |

- If no registered schemas exist, it displays "No rows".
- Clicking the `REGISTER` button allows new schema registration
- The `RE-REGISTER ALL` button registers all Credential Schemas on the ledger again.

#### ▸ Credential Schema Registration

<img src="./images/3-5-2-2.credential-schema-registration.png" width="700"/>

| Item         | Description                                                           |
|--------------|----------------------------------------------------------------|
| **Name**     | Schema name                                                    |
| **Version**  | Schema version (e.g., 1.0)                                           |
| **Tag**      | Tag for schema identification                                              |
| **Attributes**| List of configuration items (composed of Namespace ID, Caption, Label, Type) |

- Add configuration items with the `ADD ATTRIBUTE` button  
- Items are selected based on ZKP Namespace and can be reordered by dragging.

#### ▸ Credential Schema Detailed Information

<img src="./images/3-5-2-3.credential-schema-detail.png" width="700"/>

- You can verify detailed information of registered schemas.
- Each item (Label, Type, Caption) is displayed in table format.
- The `VIEW SCHEMA` button shows the schema registered on the ledger.

---

### 3.5.3. Credential Definitions

Credential Definitions is the menu for defining and managing **Credential Definitions** used for issuing ZKP-based Verifiable Credentials (VCs).

A Credential Definition is a policy specification that determines **how ZKP Credentials are generated and verified**, based on a specific ZKP Credential Schema.
This definition serves as **reference information for generating ZKP Credentials during VC issuance**.

#### ZKP Credential Generation and Delivery at Issuance

An **Issue Profile** configured for ZKP issuance references this Definition.
When a user receives a VC based on that profile, the system generates the following two credentials:

1. A standard Verifiable Credential (in JSON-LD or JWT format)
2. A ZKP Credential (in a structured ZKP format, e.g., data for CL proof)

The generated ZKP Credential follows the structure and policy defined in the Definition and is
**immediately delivered to the user upon issuance**.
The user can later present this credential to a verifier to perform **proof via Zero-Knowledge Proof**.

The Definition itself is published to the blockchain and can be referenced,
serving as **reference information for generating ZKP Credentials and verifying them within the system**.

#### Components of a Credential Definition

* **Credential Schema**: The ZKP Credential Schema this definition is based on
* **Definition Alias**: A human-readable alias used to distinguish the definition
* **Definition Version**: Version information of the definition
* **Definition Type**: Proof method (e.g., `CL`)
* **Definition Tag**: Tag used for management and search purposes

> This menu is **available only after completing Issuer registration and ZKP Credential Schema registration**.
> A Credential Definition is **used as reference information for generating and delivering ZKP Credentials to users during VC issuance**.

#### ▸ Credential Definition List

<img src="./images/3-5-3-1.credential-definition-management.png" width="700"/>

- If no registered Definitions exist, it displays "No rows".
- The list shows the Definition ID, Schema Name, Version, Tag, Status, and registration/modification date and time.
- Clicking the `REGISTER` button allows new Definition registration
- The `RE-REGISTER ALL` button registers all Credential Definitions on the ledger again.

#### ▸ Credential Definition Registration

<img src="./images/3-5-3-2.credential-definition-registration.png" width="500"/>

| Item               | Description                                               |
|--------------------|----------------------------------------------------|
| **Credential Schema** | Select registered schema (dropdown method)               |
| **Definition Alias** | Alias for identifying the definition                                 |
| **Definition Version** | Definition version                                        |
| **Definition Type** | Usage method selection (e.g., CL)                            |
| **Definition Tag** | Tag information                                           |

- Click the `REGISTER` button after completing registration
- `Check Availability` can verify Definition Alias duplication

#### ▸ Credential Definition Detailed Information

<img src="./images/3-5-3-3.credential-definition-detail.png" width="500"/>

- You can verify information such as Definition ID, Alias, linked Schema, status, etc.
- Defined content cannot be deleted and can only be viewed

---

## 3.6. User Management

User Management is the menu for **viewing user information referenced during Verifiable Credential (VC) issuance**.
User information includes **DID, PII, and VC data (claims)** required for issuing VCs,
and this information is automatically registered in the system at the time of issuance.

Registered users are treated as VC recipients, and the system uses their information to **generate personalized VCs**.
User information is linked to a VC Schema and is used to extract and validate field values during VC creation.

> **Note:** When the User Query Method of an Issue Profile is `TEST`, VCs can be issued without registering user information in advance.

#### ▸ User List

<img src="./images/3-6-1.user-management.png" width="700"/>

| Item | Description |
|------|------|
| **DID** | User's DID |
| **VC Schema** | ID of the VC Schema the user is linked to |
| **Registered At** | Initial registration date and time |
| **Updated At** | Last modification date and time |
| **View** | Opens the user detail screen. |

#### ▸ User Detailed Information

<img src="./images/3-6-2.user-detail.png" width="500"/>

| Item | Description |
|------|------|
| **DID** | User's unique identifier (Decentralized Identifier) |
| **VC Schema Name** | VC Schema ID referenced by this user's VC |
| **PII** | Hash of the user's Personal Identifiable Info |
| **User VC Info** | VC data to be issued to the user (JSON format) |
| **Created At** | User registration date and time |
| **Updated At** | Last modification date and time |

---

## 3.7. Issued Credentials

The Issued Credentials menu lets you view issued credentials by issuance protocol and manage their status.

- **OID4VCI Credentials**: Issuance history and Status List status of SD-JWT VCs and mdocs issued over OID4VCI
- **OpenDID Credentials**: VCs issued over the OpenDID protocol

### 3.7.1. OID4VCI Credentials

This screen searches the issuance history of credentials issued over OID4VCI, shows their details, and changes their Status List status.

#### ▸ OID4VCI Credential List

<img src="./images/3-7-1-1.oid4vci-credentials.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **Issuance ID** | Unique identifier of the issuance. Clicking moves to the detailed screen. |
| **User ID** | ID of the user who received the credential. |
| **Configuration ID** | Credential Configuration ID used for issuance. |
| **Format** | Format of the issued credential. (e.g., `dc+sd-jwt-did`, `mso_mdoc-did`) |
| **Issuance State** | Processing state of the issuance. (`ALLOCATED`, `ISSUED`, `FAILED`) |
| **Credential Status** | Credential status recorded in the Status List. (`VALID`, `SUSPENDED`, `INVALID`, `RESERVED`) |
| **Issued At** | Issuance date and time. |

- You can search by Issuance ID, User ID, Configuration ID, Format, Issuance State, or Credential Status. Format, Issuance State, and Credential Status are selected from a list.
- The list is paginated on the server.

#### ▸ Credential Status Change

<img src="./images/3-7-1-2.oid4vci-credential-status-change.png" width="700"/>

Select an item in the list and click the `CHANGE STATUS` button to open the **Change Credential Status** dialog.

| Current Status | Changeable Status |
| -------------- | ----------------- |
| `VALID` | `SUSPENDED`, `INVALID` |
| `SUSPENDED` | `VALID`, `INVALID` |
| `INVALID` | (Cannot be changed) |

| Item | Description |
| ---- | ----------- |
| **Status** | Status to change to. |
| **Reason** | Reason for the change. Required, up to 512 characters, and cannot be only whitespace. |

> **Note**  
> - Only credentials whose Issuance State is `ISSUED` can change status.  
> - A status change updates the value at the credential's Status List index and the Status List version, and stores the change history with the reason.  
> - Revocation requested directly by a wallet is handled by the Credential Revocation Endpoint in the Issuer Metadata and sets the status to `INVALID`.

#### ▸ OID4VCI Credential Detailed Information

<img src="./images/3-7-1-3.oid4vci-credential-detail.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **Issuance ID / User ID** | Issuance identifier and user ID. |
| **Configuration ID / Format** | Configuration and credential format used for issuance. |
| **Issuance State / Credential Status** | Processing state of the issuance and Status List status. |
| **Issued At / Expires At** | Issuance and expiration date and time. |
| **Status Changed At** | Date and time of the last status change. |
| **Status List URI / Status List Index** | URI and index of the Status List referenced by the credential. |
| **Token Hash** | Hash of the issued credential token. |
| **Failed At / Failure Code / Failure Message** | Failure time and cause when issuance failed. |

- Click `BACK` to return to the list.

---

### 3.7.2. OpenDID Credentials

OpenDID Credentials is a function that allows you to view the list of Verifiable Credentials (VCs) issued over the OpenDID protocol and manage their status.  
This screen provides an **UPDATE function** for changing VC status.

#### ▸ OpenDID Credential List

<img src="./images/3-7-2-1.opendid-credentials.png" width="700"/>

| Item | Description |
|------|------|
| **VC ID** | VC unique identifier (moves to details) |
| **DID** | DID of the user who received the VC |
| **VC Schema ID** | Schema ID of the issued VC |
| **Status** | VC status |
| **Registered At** | Issuance date and time |

- When you select an item from the list, the `UPDATE` button at the top becomes active.

#### ▸ OpenDID Credential Status Change

You can change the status of the selected VC. Status changes are possible according to the following conditions.

<img src="./images/3-7-2-2.opendid-credential-status-change.png" width="700"/>

| Current Status | Changeable Status |
|-----------|----------------|
| `ACTIVE` | `INACTIVE`, `REVOKED` |
| `INACTIVE` | `ACTIVE`, `REVOKED` |
| `REVOKED` | (Cannot be changed) |

> **Note**  
> The `REVOKED` status is immutable and cannot be recovered or changed.  
> Status changes are functions for managing and tracking the validity of user VCs.

1. Select an item from the VC list and click the `UPDATE` button  
2. Select status in the **Change VC Status** dialog  
3. After confirmation and saving, the VC status is changed

#### ▸ OpenDID Credential Detailed Information

<img src="./images/3-7-2-3.opendid-credential-detail.png" width="500"/>

| Item | Description |
|------|------|
| **VC ID** | Unique identifier |
| **DID** | User DID |
| **VC Schema** | VC schema that was issued |
| **Status** | VC status (`ACTIVE`, `REVOKE`, etc.) |
| **Created At / Updated At** | Creation and modification time |

---

## 3.8. Server Configuration

The `/server-configuration` screen manages the runtime settings that the Issuer server uses to connect to external servers. Values are read and saved through `/issuer/admin/v1/server-configs`, and unknown keys are rejected.

<img src="./images/3-8-1.server-configuration.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **API Key Settings - Ledger Service Server** | API key sent in the `X-API-Key` header when connecting to the Ledger Service Server (LSS). (`API_KEY_LSS`) |
| **UPDATE Button** | Saves the entered value. |
| **RESET Button** | Restores the saved value. |

> **Note:** When the server runs with the `lss` profile, VC Schema and Credential Schema/Definition registration use the LSS, so the API key must be set correctly.

---

## 3.9. Admin Management

The `Admin Management` menu is the top-level menu for administrator accounts and the password policy, and contains the following two sub-menus.

- Admin Management
- Password Policy Settings

### 3.9.1. Admin Management

The `Admin Management` screen is a function for managing administrator accounts that can access the Issuer Admin Console.  

When you install the Issuer server, the `admin@opendid.omnione.net` account is automatically created with ROOT privileges by default.  
This account is the only ROOT account in the system and cannot be deleted. Its ID and password must be changed on the first login.

Administrator accounts are divided into two privilege types: **ROOT** and **Normal Admin**.  
ROOT accounts can perform all functions in the `Admin Management` menu, while Normal Admin accounts can only perform general inquiry functions.

---
> **Note:** Currently, the difference in privileges between ROOT accounts and Normal Admin accounts is  
> only the difference in buttons displayed in the `Admin Management` menu (only Root can REGISTER / DELETE / CHANGE PASSWORD).  
> Access permissions or functional restrictions for other menus in the system are not yet applied.
---

<br/>

#### ▸ Admin List Inquiry


When you enter the `Admin Management` menu, a list of registered administrator accounts is displayed in table format.

<img src="./images/3-9-1.admin-management.png" width="700"/>


| Number | Item                    | Description                                                             |
| ---- | ----------------------- | ---------------------------------------------------------------- |
| 1    | **REGISTER Button**       | Moves to the registration page where you can register a new administrator account.       |
| 2    | **DELETE Button**         | Deletes the selected administrator account. (Only available to ROOT administrators)              |
| 3    | **CHANGE PASSWORD Button** | Allows you to change the password of the selected administrator account.                |
| 4    | **ID**                  | Email ID of the registered administrator account.                              |
| 5    | **Role**                | Role of the administrator account. (e.g., ROOT, Normal Admin, etc.)   |
| 6    | **Registered At**       | Date and time when the account was initially registered.                               |
| 7    | **Updated At**          | Date and time of last modification.                                       |

<br/>

#### ▸ Admin Registration

Clicking the **REGISTER** button on the `Admin Management` screen moves to the registration screen as shown below.

<img src="./images/3-9-2.admin-registration.png" width="500"/>

| Number | Item                        | Description                                                                |
| ---- | --------------------------- | ------------------------------------------------------------------- |
| 1    | **ID**                      | ID of the administrator account to register. Must use email format.         |
| 2    | **Check Availability Button** | Verifies that the entered ID is not duplicated.                             |
| 3    | **Role**                    | Select the privileges of the administrator account to register. (e.g., Normal Admin)           |
| 4    | **Password**                | Enter the password to be used for login.                              |
| 5    | **Re-enter Password**       | Enter the password again to verify if it matches.                |
| 6    | **REGISTER Button**           | Registers the administrator account based on the entered information.                    |
| 7    | **RESET Button**              | Initializes all input values.                                         |
| 8    | **CANCEL Button**             | Cancels registration and returns to the previous screen.                           |


### 3.9.2. Password Policy Settings

The `Admin Management > Password Policy Settings` screen configures the length, character rules, and expiration period of administrator passwords. Updated rules apply to subsequent password validation and changes.

<img src="./images/3-9-3.password-policy-settings.png" width="700"/>

| Item | Description |
| ---- | ----------- |
| **Minimum Length** | Minimum password length. (6–20 characters) |
| **Password Expiration Period** | Password expiration period in days. (30–365 days) When it expires, a password reset is required at the next login. |
| **Uppercase Letters (A-Z)** | Whether uppercase letters are required. |
| **Numbers (0-9)** | Whether numbers are required. |
| **Special Characters (!@#$%^&*)** | Whether special characters are required. |

[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/unrelease-V2.0.0.0/OepnDID_Installation_Guide-V2.0.0.0_ko.md
