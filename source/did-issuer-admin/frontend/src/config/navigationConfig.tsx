import StorageIcon from '@mui/icons-material/Storage';
import DescriptionIcon from '@mui/icons-material/Description';
import CategoryIcon from '@mui/icons-material/Category';
import SchemaIcon from '@mui/icons-material/Schema';
import AssignmentIcon from '@mui/icons-material/Assignment';
import { type Navigation } from '@toolpad/core/AppProvider';
import PeopleIcon from '@mui/icons-material/People';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';
import DnsIcon from '@mui/icons-material/Dns';

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
    {
      segment: 'vc-management',
      title: 'VC Management',
      icon: <DescriptionIcon />,
      children: [
        { segment: 'namespace-management', title: 'Namespace Management', icon: <CategoryIcon /> },
        { segment: 'vc-schema-management', title: 'VC Schema Management', icon: <SchemaIcon /> },
        { segment: 'issue-profile-management', title: 'Issue Profile Management', icon: <AssignmentIcon /> },
      ],
    },
    {
      segment: 'users/user-management',
      title: 'User Management',
      icon: <PeopleIcon />,
    },
    {
      segment: 'issued-vcs/issued-vc-management',
      title: 'Issued VC Management',
      icon: <DescriptionIcon />,
    },
    {
      segment: 'admin-management',
      title: 'Admin Management', 
      icon: <SupervisorAccountIcon />,
    },
    {
      segment: 'server-management',
      title: 'Server Management', 
      icon: <DnsIcon />,
    },
  ];
};
