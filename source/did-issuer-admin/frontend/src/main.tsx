import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router';
import App from './App';
import Layout from './layout/Layout';
import SignInPage from './pages/auth/SignIn';
import ErrorPage from './pages/ErrorPage';
import IssuerManagementPage from './pages/issuer/IssuerManagementPage';
import IssuerRegistrationPage from './pages/issuer/IssuerRegistrationPage';

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
