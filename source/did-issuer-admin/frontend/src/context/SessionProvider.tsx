import React, { useCallback, useEffect, useState } from 'react';
import type { ExtendedSession, SessionContextValue } from './SessionContext';
import { SessionContext } from './SessionContext';
import { useIdleLogout } from '../hooks/useIdleLogout';
import { tokenStore } from '../utils/authTokens';
import { restoreSessionFromToken } from '../utils/authSync';
import LoadingOverlay from '../components/loading/LoadingOverlay';
import { ADMIN_LOGOUT_EVENT, logoutAdmin } from '../utils/logout';

export const SessionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [session, setSessionState] = useState<ExtendedSession | null>(() => {
    const storedSession = sessionStorage.getItem('session') ?? localStorage.getItem('session');
    return storedSession && tokenStore.getRefresh() ? JSON.parse(storedSession) : null;
  });
  const [isRestoring, setIsRestoring] = useState(() => !session && !!tokenStore.getRefresh());

  const logout = useCallback(async () => {
    if (tokenStore.isPendingLogout()) {
      sessionStorage.removeItem('session');
      setSessionState(null);
      window.location.replace('/sign-in');
      return;
    }
    await logoutAdmin('/issuer/admin/v1');
    setSessionState(null);
    alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
    window.location.replace('/sign-in');
  }, []);

  useEffect(() => {
    const onStorage = (event: StorageEvent) => {
      if (event.key === ADMIN_LOGOUT_EVENT && event.newValue) {
        tokenStore.markRemoteLogout(event.newValue);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      sessionStorage.setItem('session', JSON.stringify(newSession));
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      sessionStorage.removeItem('session');
      localStorage.removeItem('session');
      tokenStore.clear();
    }
  }, []);

  useEffect(() => {
    if (session || !tokenStore.getRefresh()) return;
    void restoreSessionFromToken('/issuer/admin/v1')
      .then((restored) => { if (restored) setSession(restored); })
      .finally(() => setIsRestoring(false));
  }, []);

  useIdleLogout(session !== null, logout);

  const contextValue: SessionContextValue = { session, setSession };
  if (isRestoring) return <LoadingOverlay />;
  return <SessionContext.Provider value={contextValue}>{children}</SessionContext.Provider>;
};
