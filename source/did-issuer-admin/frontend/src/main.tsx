import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import AdminManagementPage from './pages/admins/AdminManagementPage';
import SignInPage from './pages/auth/SignIn';
import ErrorPage from './pages/ErrorPage';
import IssuedVcManagementPage from './pages/issued-vcs/IssuedVcManagementPage';
import IssuerManagementPage from './pages/issuer/IssuerManagementPage';
import IssuerRegistrationPage from './pages/issuer/IssuerRegistrationPage';
import ServerManagementPage from './pages/servers/ServerManagementPage';
import UserManagementPage from './pages/users/UserManagementPage';
import IssueProfileManagementPage from './pages/vc-management/issue-profile-management/IssueProfileManagementPage';
import NamespaceDetailPage from './pages/vc-management/namespace-management/NamespaceDetailPage';
import NamespaceManagementPage from './pages/vc-management/namespace-management/NamespaceManagementPage';
import NamespaceRegistrationPage from './pages/vc-management/namespace-management/NamespaceRegistrationPage';
import VcSchemaManagementPage from './pages/vc-management/vc-schema-management/VcSchemaManagementPage';
import VcSchemaRegistrationPage from './pages/vc-management/vc-schema-management/VcSchemaRegistrationPage';
import VcManagementPage from './pages/vc-management/VcManagementPage';
import NamespaceEditPage from './pages/vc-management/namespace-management/NamespaceEditPage';
import VcSchemaDetailPage from './pages/vc-management/vc-schema-management/VcSchemaDetailPage';
import VcSchemaEditPage from './pages/vc-management/vc-schema-management/VcSchemaEditPage';
import IssueProfileEditPage from './pages/vc-management/issue-profile-management/IssueProfileEditPage';
import IssueProfileDetailPage from './pages/vc-management/issue-profile-management/IssueProfileDetailPage';
import IssueProfileRegistrationPage from './pages/vc-management/issue-profile-management/IssueProfileRegistrationPage';
import UserRegistrationPage from './pages/users/UserRegistrationPage';
import UserDetailPage from './pages/users/UserDetailPage';
import UserEditPage from './pages/users/UserEditPage';
import IssuedVcDetailPage from './pages/issued-vcs/IssuedVcDetailPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import AdminRegisterPage from './pages/admins/AdminRegisterPage';
import AdminDetailPage from './pages/admins/AdminDetailPage';
import ZkpNamespaceManagementPage from './pages/zkp-management/namespace-management/ZkpNamespaceManagementPage';
import CredentialSchemaManagementPage from './pages/zkp-management/credential-schema-management/CredentialSchemaManagementPage';
import CredentialDefinitionManagementPage from './pages/zkp-management/credential-definition-management/CredentialDefinitionManagementPage';
import ZkpNamespaceRegistrationPage from './pages/zkp-management/namespace-management/ZkpNamespaceRegistrationPage';
import ZkpNamespaceDetailPage from './pages/zkp-management/namespace-management/ZkpNamespaceDetailPage';
import ZkpNamespaceEditPage from './pages/zkp-management/namespace-management/ZkpNamespaceEditPage';
import ZkpCredentialSchemaRegistrationPage from './pages/zkp-management/credential-schema-management/CredentialSchemaRegistrationPage';
import CredentialSchemaDetailPage from './pages/zkp-management/credential-schema-management/CredentialSchemaDetailPage';
import CredentialDefinitionRegistrationPage from './pages/zkp-management/credential-definition-management/CredentialDefinitionRegistrationPage';
import CredentialDefinitionDetailPage from './pages/zkp-management/credential-definition-management/CredentialDefinitionDetailPage';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '/',
            Component: DashboardPage
          },
          {
            path: '/issuer-registration',
            Component: IssuerRegistrationPage,
          },
          {
            path: '/issuer-management',
            Component: IssuerManagementPage,
          },
          // Namespace
          {
            path: '/vc-management/namespace-management/namespace-registration',
            Component: NamespaceRegistrationPage,
          },
          {
            path: '/vc-management/namespace-management/namespace-edit/:id',
            Component: NamespaceEditPage,
          },
          {
            path: '/vc-management/namespace-management/:id',
            Component: NamespaceDetailPage,
          },
          {
            path: '/vc-management/namespace-management',
            Component: NamespaceManagementPage,
          },
          // VC Schema
          {
            path: '/vc-management/vc-schema-management/vc-schema-registration',
            Component: VcSchemaRegistrationPage,
          },
          {
            path: '/vc-management/vc-schema-management/vc-schema-edit/:id',
            Component: VcSchemaEditPage,
          },
          {
            path: '/vc-management/vc-schema-management/:id',
            Component: VcSchemaDetailPage,
          },
          {
            path: '/vc-management/vc-schema-management',
            Component: VcSchemaManagementPage,
          },
          // Issue Profile
          {
            path: '/vc-management/issue-profile-management/issue-profile-registration',
            Component: IssueProfileRegistrationPage,
          },
          {
            path: '/vc-management/issue-profile-management/issue-profile-edit/:id',
            Component: IssueProfileEditPage,
          },
          {
            path: '/vc-management/issue-profile-management/:id',
            Component: IssueProfileDetailPage,
          },
          {
            path: '/vc-management/issue-profile-management',
            Component: IssueProfileManagementPage,
          },


          // vc management
          {
            path: '/vc-management',
            Component: VcManagementPage,
          },

          // user management
          {
            path: '/users/user-management/user-registration',
            Component: UserRegistrationPage
          },
          {
            path: '/users/user-management/user-edit/:id',
            Component: UserEditPage
          },
          {
            path: '/users/user-management/:id',
            Component: UserDetailPage
          },
          {
            path: '/users/user-management',
            Component: UserManagementPage,
          },
          {
            path: '/issued-vcs/issued-vc-management/:id',
            Component: IssuedVcDetailPage,
          },
          {
            path: '/issued-vcs/issued-vc-management',
            Component: IssuedVcManagementPage,
          },
          {
            path: '/admin-management/admin-registration',
            Component: AdminRegisterPage,
          },
          {
            path: '/admin-management/:id',
            Component: AdminDetailPage,
          },

          // admin management
          {
            path: '/admin-management',
            Component: AdminManagementPage,
          },

          // server management
          {
            path: '/server-management',
            Component: ServerManagementPage,
          },

          // zkp namespace management
          {
            path: '/zkp-management/zkp-namespace-management/namespace-registration',
            Component: ZkpNamespaceRegistrationPage,
          },
          {
            path: '/zkp-management/zkp-namespace-management/zkp-namespace-edit/:id',
            Component: ZkpNamespaceEditPage,
          },
          {
            path: '/zkp-management/zkp-namespace-management/:id',
            Component: ZkpNamespaceDetailPage,
          },
          
          {
            path: '/zkp-management/zkp-namespace-management',
            Component: ZkpNamespaceManagementPage,
          },

          // zkp credential schema management
          {
            path: '/zkp-management/credential-schema-management/credential-schema-registration',
            Component: ZkpCredentialSchemaRegistrationPage,
          },
          {
            path: '/zkp-management/credential-schema-management/:id',
            Component: CredentialSchemaDetailPage,
          },
          {
            path: '/zkp-management/credential-schema-management',
            Component: CredentialSchemaManagementPage,
          },

          // zkp credential definition management
          {
            path: '/zkp-management/credential-definition-management/credential-definition-registration',
            Component: CredentialDefinitionRegistrationPage,
          },
          {
            path: '/zkp-management/credential-definition-management/:id',
            Component: CredentialDefinitionDetailPage,
          },
          {
            path: '/zkp-management/credential-definition-management',
            Component: CredentialDefinitionManagementPage,
          },
          {
            path: '/zkp-management',
            Component: ZkpNamespaceManagementPage,
          },
        ],
      },
      {
        path: '/vc-management/namespace-management-popup/:id',
        Component: NamespaceDetailPage,
      },
      {
        path: '/vc-management/vc-schema-management-popup/:id',
        Component: VcSchemaDetailPage,
      },
      {
        path: '/sign-in',
        Component: SignInPage,
      },
      {
        path: '/error',
        Component: ErrorPage,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);
