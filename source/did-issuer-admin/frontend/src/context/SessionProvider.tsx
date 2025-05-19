// SessionProvider.tsx
import React, { useCallback, useEffect, useRef, useState } from 'react';
import type { ExtendedSession, SessionContextValue } from './SessionContext';
import { SessionContext } from './SessionContext';

const SESSION_TIMEOUT = 30 * 60 * 1000; // 30분

export const SessionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [session, setSessionState] = useState<ExtendedSession | null>(null);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const logout = useCallback(() => {
    setSessionState(null);
    localStorage.removeItem('session');
    sessionStorage.removeItem('session');
    alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
    window.location.href = '/'; // or use navigate('/login')
  }, []);

  const resetTimeout = useCallback(() => {
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      logout();
    }, SESSION_TIMEOUT);
  }, [logout]);

  const setSession = useCallback((newSession: ExtendedSession | null) => {
    setSessionState(newSession);
    if (newSession) {
      const storage = localStorage.getItem('rememberMe') === 'true' ? localStorage : sessionStorage;
      storage.setItem('session', JSON.stringify(newSession));
      resetTimeout();
    } else {
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    }
  }, [resetTimeout]);

  // 사용자 활동 감지 → 타임아웃 초기화
  useEffect(() => {
    if (!session) return;

    const onActivity = () => resetTimeout();
    window.addEventListener('mousemove', onActivity);
    window.addEventListener('keydown', onActivity);
    window.addEventListener('click', onActivity);

    resetTimeout(); // 최초 로그인 시도

    return () => {
      window.removeEventListener('mousemove', onActivity);
      window.removeEventListener('keydown', onActivity);
      window.removeEventListener('click', onActivity);
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
  }, [session, resetTimeout]);

  // 초기 세션 복구
  useEffect(() => {
    const storedSession =
      localStorage.getItem('session') || sessionStorage.getItem('session');
    if (storedSession) {
      setSessionState(JSON.parse(storedSession));
    }
  }, []);

  const contextValue: SessionContextValue = {
    session,
    setSession,
  };

  return (
    <SessionContext.Provider value={contextValue}>
      {children}
    </SessionContext.Provider>
  );
};
