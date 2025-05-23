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

export const verifyNamespaceIdUnique = async (namespaceId: string) => {
    return getData(API_BASE_URL, `zkp/namespaces/check-namespace-id?namespaceId=${namespaceId}`);
}

export const getZkpNamespaceAll = async () => {
    return getData(API_BASE_URL, `zkp/namespaces/all`);
}

export const fetchZkpSchemas = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `zkp/schemas?${params.toString()}`);
};

export const getZkpAttributes = async (namespaceId: number) => {
    return getData(API_BASE_URL, `zkp/namespaces/attributes/all?id=${namespaceId}`);
}

export const postZkpSchema = async (data: any) => {
    return postData(API_BASE_URL, `zkp/schemas`, data);
}

export const getZkpSchema = async (id: number) => {
    return getData(API_BASE_URL, `zkp/schemas/${id}`);
};

export const fetchCredentialDefinitions = async (page: number, size: number, searchKey: string | null, searchValue: string | null) => {
    const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
    });

    if (searchKey && searchValue) {
        params.append("searchKey", searchKey);
        params.append("searchValue", searchValue);
    }

    return getData(API_BASE_URL, `zkp/definitions?${params.toString()}`);
};

export const postCredentialDefinition = async (data: any) => {
    return postData(API_BASE_URL, `zkp/definitions`, data);
}

export const getZkpSchemaAll = async () => {
    return getData(API_BASE_URL, `zkp/schemas/all`);
}

export const verifyCredentialDefinitionAliasUnique = async (alias: string) => {
    return getData(API_BASE_URL, `zkp/definitions/check-credential-definition-alias?alias=${alias}`);
}

export const getCredentialDefinition = async (id: number) => {
    return getData(API_BASE_URL, `zkp/definitions/${id}`);
};

export const postReRegisterSchema = async () => {

    return postData(API_BASE_URL, "zkp/schemas/re-register", null)
}

export const postReRegisterDefinition = async () => {

    return postData(API_BASE_URL, "zkp/definitions/re-register", null)
}