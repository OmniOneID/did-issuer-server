import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
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

function AppContent() {
  const navigate = useNavigate();
  const { serverStatus, setServerStatus, setIssuerInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const { session, setSession } = useSession(); // 🔄 기존 useState 제거

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(() => {
    setSession(null); // 🔄 context에서 관리
    navigate('/sign-in');
  }, [navigate, setSession]);

  useEffect(() => {
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
  }, []);

  useEffect(() => {
    if (serverStatus !== null) {
      setNavigation(getNavigationByStatus(serverStatus));
    }
  }, [serverStatus]);

  if (isLoading) {
    return <LoadingOverlay />;
  }

  return (
    <DialogsProvider>
      <ReactRouterAppProvider
        navigation={navigation}
        session={session}
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
    <SessionProvider>
      <ServerStatusProvider>
        <GlobalStyles styles={{ body: { padding: "10px" } }} />
        <AppContent />
      </ServerStatusProvider>
    </SessionProvider>
  );
}
