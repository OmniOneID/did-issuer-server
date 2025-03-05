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
import VcManagementPage from './pages/vc-management/VcManagementPage';
import NamespaceEditPage from './pages/vc-management/namespace-management/NamespaceEditPage';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '/issuer-registration',
            Component: IssuerRegistrationPage,
          },
          {
            path: '/issuer-management',
            Component: IssuerManagementPage,
          },
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
          {
            path: '/vc-management/vc-schema-management',
            Component: VcSchemaManagementPage,
          },
          {
            path: '/vc-management/issue-profile-management',
            Component: IssueProfileManagementPage,
          },
          {
            path: '/vc-management',
            Component: VcManagementPage,
          },
          {
            path: '/users/user-management',
            Component: UserManagementPage,
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
