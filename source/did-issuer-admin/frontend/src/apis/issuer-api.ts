import { getData, postData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const getIssuerInfo = async () => {
    return getData(API_BASE_URL, "issuer/info");
}

export const registerIssuerInfo = async (data: any) => {
    return postData(API_BASE_URL, `issuer/register-issuer-info`, data);
};

export const generateIssuerDidDocument = async () => {
    return postData(API_BASE_URL, `issuer/generate-did-auto`, undefined);
}

export const registerIssuerDidDocument = async (data: any) => {
    return postData(API_BASE_URL, `issuer/register-did`, data);
};

export const requestEntityStatus = async () => {
    return getData(API_BASE_URL, "issuer/request-status");
}

export const requestEnrollEntity = async () => {
    return postData(API_BASE_URL, "issuer/request-enroll-entity", undefined);
}