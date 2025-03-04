import * as React from 'react';
import type { Session } from '@toolpad/core';

export interface SessionContextValue {
  session: Session | null;
  setSession: (session: Session | null) => void;
}

export const SessionContext = React.createContext<SessionContextValue>({
  session: null,
  setSession: () => {},
});

export function useSession() {
  return React.useContext(SessionContext);
}
