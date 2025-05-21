import { type Navigation } from '@toolpad/core/AppProvider';

export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'ACTIVATE') {
    return [
      {kind: 'divider'},
      { segment: 'issuer-registration', title: 'Issuer Registration',},
      {kind: 'divider'},
    ];
  } 
  return [
    {kind: 'divider'},
    { 
      segment: 'issuer-management', 
      title: 'Issuer Management', 
    },
    {
      segment: 'vc-management',
      title: 'VC Management',
      children: [
        { segment: 'namespace-management', title: 'Namespace Management',},
        { segment: 'vc-schema-management', title: 'VC Schema Management', },
        { segment: 'issue-profile-management', title: 'Issue Profile Management',},
      ],
    },
    {
      segment: 'zkp-management',
      title: 'ZKP Management',
      children: [
        { segment: 'zkp-namespace-management', title: 'ZKP Namespace Management',},
        { segment: 'credential-schema-management', title: 'Credential Schema Management', },
        { segment: 'credential-definition-management', title: 'Credential Definition Management',},
      ],
    },
    {
      segment: 'users/user-management',
      title: 'User Management',
    },
    {
      segment: 'issued-vcs/issued-vc-management',
      title: 'Issued VC Management',
    },
    {
      segment: 'admin-management',
      title: 'Admin Management', 
    },
    // {
    //   segment: 'server-management',
    //   title: 'Server Management', 
    // },
    {kind: 'divider'},
  ];
};
