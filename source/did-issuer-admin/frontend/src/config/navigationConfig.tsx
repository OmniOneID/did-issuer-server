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
        segment: 'oid4vci-management',
        title: 'OID4VCI',
        children: [
            { segment: 'metadata', title: 'Issuer Metadata', },
            { segment: 'credential-config', title: 'Credential Configurations', },
            { segment: 'property', title: 'Client Settings', },
            { segment: 'list-provider-registration', title: 'List Provider', },
        ],
    },
    {
      segment: 'opendid',
      title: 'OpenDID',
      children: [
        {
          segment: 'vc',
          title: 'VC',
          children: [
            { segment: 'namespaces', title: 'Namespaces', },
            { segment: 'vc-schemas', title: 'VC Schemas', },
            { segment: 'issuance-profiles', title: 'Issuance Profiles', },
          ],
        },
        {
          segment: 'zkp',
          title: 'ZKP',
          children: [
            { segment: 'namespaces', title: 'Namespaces', },
            { segment: 'credential-schemas', title: 'Credential Schemas', },
            { segment: 'credential-definitions', title: 'Credential Definitions', },
          ],
        },
      ],
    },
    {
      segment: 'users/user-management',
      title: 'User Management',
    },
    {
      segment: 'issued-credentials',
      title: 'Issued Credentials',
      children: [
        { segment: 'oid4vci', title: 'OID4VCI Credentials', },
        { segment: 'opendid', title: 'OpenDID Credentials', },
      ],
    },
    {
      segment: 'server-configuration',
      title: 'Server Configuration',
    },
    {
      segment: 'admins',
      title: 'Admin Management',
      children: [
        { segment: 'admin-management', title: 'Admin Management' },
        { segment: 'password-policy', title: 'Password Policy Settings' },
      ],
    },
    {kind: 'divider'},
  ];
};
