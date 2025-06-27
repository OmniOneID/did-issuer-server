import React, { createContext, useContext, useState, ReactNode, useCallback } from 'react';
import { IssuerInfoResDto } from '../apis/models/IssuerInfoResDto';

export type ServerStatus = 'ACTIVATE' | 'DEACTIVATE' | 'REQUIRED_ENROLL_ENTITY';

interface ServerStatusContextType {
  serverStatus: ServerStatus | null;
  setServerStatus: (status: ServerStatus | null) => void;
  isLoading: boolean;
  setIsLoading: (loading: boolean, message?: string) => void;
  isLoadingMessage: string;
  issuerInfo: IssuerInfoResDto | null;
  setIssuerInfo: (info: IssuerInfoResDto | null) => void;
}

export const ServerStatusContext = createContext<ServerStatusContextType>({
  serverStatus: null,
  setServerStatus: () => {},
  isLoading: false,
  setIsLoading: () => {},
  isLoadingMessage: '',
  issuerInfo: null,
  setIssuerInfo: () => {},
});

export const ServerStatusProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [serverStatus, setServerStatus] = useState<ServerStatus | null>(null);
  const [isLoading, setIsLoadingState] = useState<boolean>(false);
  const [isLoadingMessage, setIsLoadingMessage] = useState<string>('');
  const [issuerInfo, setIssuerInfo] = useState<IssuerInfoResDto | null>(null);

  const setIsLoading = useCallback((loading: boolean, message?: string) => {
    setIsLoadingState(loading);
    setIsLoadingMessage(message ?? '처리 중입니다...');
  }, []);

  return (
    <ServerStatusContext.Provider 
    value={{ 
        serverStatus, 
        setServerStatus, 
        isLoading, 
        setIsLoading, 
        isLoadingMessage,
        issuerInfo: issuerInfo,
        setIssuerInfo: setIssuerInfo,
      }}
    >
      {children}
    </ServerStatusContext.Provider>
  );
};

export const useServerStatus = () => useContext(ServerStatusContext);
