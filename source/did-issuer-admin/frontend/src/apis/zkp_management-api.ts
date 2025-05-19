import { getData, postData, patchData, deleteData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const fetchZkpNamespaces = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `zkp/namespaces?${params.toString()}`);
};

export const zkpDeleteNamespace = async (id: number) => {  
    return deleteData(API_BASE_URL, `zkp/namespaces?id=${id}`);
}

export const postNamespace = async (data: any) => {
    return postData(API_BASE_URL, `zkp/namespaces`, data);
};

export const getZkpNamespace = async (id: number) => {
    return getData(API_BASE_URL, `zkp/namespaces/${id}`);
}

export const patchZkpNamespace = async (data: any) => {
    return patchData(API_BASE_URL, `zkp/namespaces`, data);
}