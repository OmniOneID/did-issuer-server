import { getData, patchData } from '../utils/api';

const API_BASE_URL = '/issuer/admin/v1';

export const fetchOid4vciIssuedCredentials = async (
  page: number,
  size: number,
  searchKey: string | null,
  searchValue: string | null,
) => {
  const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
  if (searchKey && searchValue) {
    params.append('searchKey', searchKey);
    params.append('searchValue', searchValue);
  }
  return getData(API_BASE_URL, `oid4vci/issued-credentials?${params.toString()}`);
};

export const getOid4vciIssuedCredential = async (id: number) =>
  getData(API_BASE_URL, `oid4vci/issued-credentials/${id}`);

export const changeOid4vciCredentialStatus = async (
  id: number,
  status: string,
  reason: string,
) => patchData(API_BASE_URL, `oid4vci/issued-credentials/${id}/status`, { status, reason });
