import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { PasswordPolicyProvider } from './context/PasswordPolicyContext';
import { Outlet, useNavigate } from 'react-router';
import { ExtendedSession, SessionContext, useSession } from './context/SessionContext';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { getNavigationByStatus } from './config/navigationConfig';
import LoadingOverlay from './components/loading/LoadingOverlay';
import { getIssuerInfo } from './apis/issuer-api';
import customTheme from './theme';
import { CssBaseline, GlobalStyles } from '@mui/material';
import { formatErrorMessage } from './utils/error-handler';
import { SessionProvider } from './context/SessionProvider';
import { logoutAdmin } from './utils/logout';
import { tokenStore } from './utils/authTokens';

function AppContent() {
  const navigate = useNavigate();
  const { serverStatus, setServerStatus, setIssuerInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const { session, setSession } = useSession(); // 🔄 기존 useState 제거

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(async () => {
    if (tokenStore.isPendingLogout()) {
      sessionStorage.removeItem('session');
      window.location.replace('/sign-in');
      return;
    }
    await logoutAdmin('/issuer/admin/v1');
    setSession(null);
    navigate('/sign-in');
  }, [navigate, setSession]);

  useEffect(() => {
    if (!session) {
      setIsLoading(false);
      return;
    }
    const fetchIssuerInfo = () => {
      setIsLoading(false);

      getIssuerInfo()
        .then(({ data }) => {
          setServerStatus(data.status);
          setIssuerInfo(data);
          setNavigation(getNavigationByStatus(data.status));
          setIsLoading(false);
        })
        .catch((err) => {
          navigate('/error', {
            state: { message: formatErrorMessage(err, 'Failed to connect server.') },
          });
          setIsLoading(false);
        });
    };

    fetchIssuerInfo();

    const handlePopState = (_event: PopStateEvent) => {
      fetchIssuerInfo();
    };
    window.addEventListener('popstate', handlePopState);
    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, [session, navigate]);

  useEffect(() => {
    if (serverStatus !== null) {
      setNavigation(getNavigationByStatus(serverStatus));
    }
  }, [serverStatus]);

  const displaySession = useMemo(() => session ? {
    ...session,
    user: {
      ...session.user,
      name: session.user?.name || session.user?.id || 'Admin',
      email: session.user?.email || session.user?.id || undefined,
    },
  } : null, [session]);

  if (isLoading) {
    return <LoadingOverlay />;
  }

  return (
    <DialogsProvider>
      <ReactRouterAppProvider
        navigation={navigation}
        session={displaySession}
        authentication={{ signIn, signOut }}
        theme={customTheme}
      >
        <CssBaseline />
        <Outlet />
      </ReactRouterAppProvider>
    </DialogsProvider>
  );
}


export default function App() {
  return (
    <PasswordPolicyProvider>
      <SessionProvider>
        <ServerStatusProvider>
          <GlobalStyles styles={{ body: { padding: "10px" } }} />
          <AppContent />
        </ServerStatusProvider>
      </SessionProvider>
    </PasswordPolicyProvider>
  );
}
