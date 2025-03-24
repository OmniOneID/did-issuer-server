import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { ExtendedSession, SessionContext } from './context/SessionContext';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { getNavigationByStatus } from './config/navigationConfig';
import LoadingOverlay from './components/loading/LoadingOverlay';
import { getIssuerInfo } from './apis/issuer-api';
import customTheme from './theme';
import { CssBaseline, GlobalStyles } from '@mui/material';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, setServerStatus, setIssuerInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const [session, setSessionState] = useState<ExtendedSession | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      localStorage.removeItem('session'); 
    }
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(() => {
    setSession(null);
    navigate('/sign-in');
  }, [navigate]);

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
        navigate('/error', { state: { message: `Failed to connect server: ${err}` } });
        setIsLoading(false);
      });
    };

    fetchIssuerInfo();

    const handlePopState = (event: PopStateEvent) => {
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

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (isLoading) {
    return <LoadingOverlay />;
  }

  return (
    <SessionContext.Provider value={sessionContextValue}>
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
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <ServerStatusProvider>
      <GlobalStyles styles={{ body: { padding: "10px" } }} />
      <AppContent />
    </ServerStatusProvider>
  );
}
