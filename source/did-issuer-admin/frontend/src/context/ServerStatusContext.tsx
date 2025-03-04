import React, { createContext, useContext, useState, ReactNode, useCallback } from 'react';
import { TaInfoResDto } from '../apis/models/IssuerInfoResDto';

export type ServerStatus = 'DID_DOCUMENT_REQUIRED' | 'CERTIFICATE_VC_REQUIRED' | 'COMPLETED';

interface ServerStatusContextType {
  serverStatus: ServerStatus | null;
  setServerStatus: (status: ServerStatus | null) => void;
  isLoading: boolean;
  setIsLoading: (loading: boolean, message?: string) => void;
  isLoadingMessage: string;
  issuerInfo: TaInfoResDto | null;
  setIssuerInfo: (info: TaInfoResDto | null) => void;
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
  const [issuerInfo, setIssuerInfo] = useState<TaInfoResDto | null>(null);

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
