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
            path: '/admin-management',
            Component: AdminManagementPage,
          },
          {
            path: '/server-management',
            Component: ServerManagementPage,
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
