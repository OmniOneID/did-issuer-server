import { getData, postData, patchData, deleteData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const fetchNamespaces = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `namespaces?${params.toString()}`);
};

export const postNamespace = async (data: any) => {
    return postData(API_BASE_URL, `namespaces`, data);
};

export const getNamespace = async (id: number) => {
    return getData(API_BASE_URL, `namespaces/${id}`);
}

export const patchNamespace = async (data: any) => {
    return patchData(API_BASE_URL, `namespaces`, data);
}

export const deleteNamespace = async (id: number) => {  
    return deleteData(API_BASE_URL, `namespaces?id=${id}`);
}

export const fetchVcSchema = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `vc-schemas?${params.toString()}`);
};

export const postVcSchema = async (data: any) => {
    return postData(API_BASE_URL, `vc-schemas`, data);
};

export const getVcSchema = async (id: number) => {
    return getData(API_BASE_URL, `vc-schemas/${id}`);
}

export const patchVcSchema = async (data: any) => {
    return patchData(API_BASE_URL, `vc-schemas`, data);
}

export const deleteVcSchema = async (id: number) => {  
    return deleteData(API_BASE_URL, `vc-schemas?id=${id}`);
}

export const fetchIssueProfile = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `issue-profiles?${params.toString()}`);
};

export const postIssueProfile = async (data: any) => {
    return postData(API_BASE_URL, `issue-profiles`, data);
};

export const getIssueProfile = async (id: number) => {
    return getData(API_BASE_URL, `issue-profiles/${id}`);
}

export const patchIssueProfile = async (data: any) => {
    return patchData(API_BASE_URL, `issue-profiles`, data);
}

export const deleteIssueProfile = async (id: number) => {  
    return deleteData(API_BASE_URL, `issue-profiles?id=${id}`);
}

export const fetchIssuedVc = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `issued-vcs?${params.toString()}`);
};

export const getIssuedVc = async (id: number) => {
    return getData(API_BASE_URL, `issued-vcs/${id}`);
}

export const updateIssuedVcStatus = async (data: any) => {
    return postData('/issuer/api/v1', `status`, data)
}