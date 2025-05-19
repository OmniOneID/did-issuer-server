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

    return getData(API_BASE_URL, `/zkp/namespaces?${params.toString()}`);
};

export const deleteNamespace = async (id: number) => {  
    return deleteData(API_BASE_URL, `/zkp/namespaces?id=${id}`);
}