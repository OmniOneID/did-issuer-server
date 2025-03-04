import { getData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1";

export const getIssuerInfo = async () => {
    return getData(API_BASE_URL, "issuer/info");
}