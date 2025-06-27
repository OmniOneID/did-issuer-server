# Issuer Admin Console Source Code

Welcome to the Issuer Admin Console source code repository. This directory contains the frontend source code and build configurations for the Issuer Admin Console, developed as part of the OpenDID project.

## Directory Structure

Here's an overview of the directory structure.

```
did-issuer-admin/frontend
├── public
├── src
│   ├── apis
│   ├── assets
│   ├── components
│   ├── config
│   │   └── navigationConfig.tsx
│   ├── constants
│   ├── context
│   ├── layout
│   │   └── Layout.tsx
│   ├── pages
│   │   ├── ErrorPage.tsx
│   │   ├── auth
│   │   ├── issued-vcs
│   │   ├── issuer
│   │   ├── users
│   ├── utils
│   ├── App.tsx
│   ├── main.tsx
│   ├── theme.ts
│   └── vite-env.d.ts
├── index.html
├── package.json
├── vite.config.ts
```

Below is a description of each folder and file in the `src` directory:

| Name                   | Description                                       |
| ---------------------- | ------------------------------------------------- |
| public                 | Static assets served as-is from the root path `/` |
| src/apis               | API client modules for Issuer Admin features      |
| src/assets             | Static assets used in the application             |
| src/components         | Reusable UI components                            |
| src/config             | Application configuration settings                |
| ┖ navigationConfig.tsx | Sidebar navigation configuration                  |
| src/constants          | Constant values and enums used across the app     |
| src/context            | React context for global state management         |
| src/layout             | Layout components including topbar and sidebar    |
| ┖ Layout.tsx           | Main layout component                             |
| src/pages              | Page-level components mapped to route segments    |
| ┖ ErrorPage.tsx        | Error fallback page                               |
| ┖ auth                 | Pages related to login and authentication         |
| ┖ issued-vcs           | Pages related to the VC Management menu           |
| ┖ issuer               | Pages related to the Issuer Management menu       |
| ┖ users                | Pages related to the User Management menu         |
| src/utils              | Utility functions and helpers                     |
| src/App.tsx            | Root component of the React application           |
| src/main.tsx           | Application bootstrap and mount logic             |
| src/theme.ts           | MUI theme configuration                           |
| src/vite-env.d.ts      | Type definitions for Vite environment             |

## Libraries

Libraries used in this project are third-party open-source dependencies managed via the [package.json](./package.json) file. Major frameworks and tools include:

- `React` (v19)
- `Vite` (v5)
- `TypeScript`
- `MUI` (Material UI v6, including X-DataGrid and X-Charts)
- `Styled-Components`
- `React Router v7`
- `@emotion/react`, `@emotion/styled`
- `core-vite-auth`: Authentication utility package for Vite-based applications

For a detailed list of third-party libraries and their licenses, please refer to the [dependencies-license.md](../../../dependencies-license.md) file.

## Documentation

Refer to the following documents for more detailed information:
- [OpenDID Issuer Admin Console Operation Guide](../../../docs/admin/OpenDID_IssuerAdmin_Operation_Guide_ko.md)

## Contributing

Please read [CONTRIBUTING.md](../../../CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](../../../CODE_OF_CONDUCT.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the [Apache 2.0](../../../LICENSE) license.

## Contact

For questions or support, please contact [maintainers](../../../MAINTAINERS.md).
