import { getData, postData, patchData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const fetchNamepsaces = async (page: number, size: number, searchKey: string|null, searchValue: string|null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `namespace/list?${params.toString()}`);
};

export const postNamespace = async (data: any) => {
    return postData(API_BASE_URL, `namespace`, data);
};

export const getNamespace = async (id: number) => {
    return getData(API_BASE_URL, `namespace?id=${id}`);
}

export const patchNamespace = async (data: any) => {
    return patchData(API_BASE_URL, `namespace`, data);
}