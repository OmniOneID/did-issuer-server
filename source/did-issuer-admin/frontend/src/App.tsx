import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { SessionContext } from './context/SessionContext';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { getNavigationByStatus } from './config/navigationConfig';
import LoadingOverlay from './components/loading/LoadingOverlay';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, setServerStatus, setIssuerInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const [session, setSessionState] = useState<Session | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const setSession = useCallback((newSession: Session | null) => {
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

  // Fetch TA information
  useEffect(() => {
    const fetchIssuerInfo = () => {
      setIsLoading(false);
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
        >
          <Outlet />
        </ReactRouterAppProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <ServerStatusProvider>
      <AppContent />
    </ServerStatusProvider>
  );
}
