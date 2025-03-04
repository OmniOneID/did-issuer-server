import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';


export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'ACTIVATE') {
    return [{ segment: 'issuer-registration', title: 'Issuer Registration', icon: <StorageIcon /> }];
  } 
  return [
    { 
      segment: 'issuer-management', 
      title: 'Issuer Management', 
      icon: <StorageIcon />,
    },
  ];
};
