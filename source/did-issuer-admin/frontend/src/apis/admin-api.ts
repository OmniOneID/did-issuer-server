import { postData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const requestLogin = async (data: any) => {
    return postData(API_BASE_URL, `login`, data);
};

export const requestPasswordReset = async (data: any) => {
    return postData(API_BASE_URL, `admins/reset-password`, data);
}