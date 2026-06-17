import { getData, postData } from "../utils/api";

const API_BASE_URL = "/issuer/admin/v1/oid4vci";

export interface CredentialRequestEncryption {
    encValuesSupported: string[];
    encryptionRequired: boolean;
}

export interface CredentialResponseEncryption {
    algValuesSupported: string[];
    encValuesSupported: string[];
    encryptionRequired: boolean;
}

export interface IssuerMetadata {
    credentialIssuer: string;
    authorizationServer: string[];
    credentialOfferEndpoint: string;
    credentialEndpoint: string;
    nonceEndpoint: string;
    deferredCredentialEndpoint: string;
    notificationEndpoint: string;
    credentialRequestEncryption: CredentialRequestEncryption;
    credentialResponseEncryption: CredentialResponseEncryption;
    credentialConfigurationsSupported: Record<string, any>;
}

export interface SdkCredentialConfig {
    id: string;
    format: string;
    identifiers: string[];
    metadataJson: string;
}

export interface ClientPlatform {
    clientId: string;
    redirectUris: string[];
    scopes: string[];
}

export interface IssuerServer {
    url: string;
}

export interface Clients {
    clientId: string;
    clientSecret: string;
    redirectUrl: string;
    redirectUris: string[];
    scopes: string[];
    android: ClientPlatform;
    androidOpenid: ClientPlatform;
    ios: ClientPlatform;
    iosOpenid: ClientPlatform;
    issuerServer: IssuerServer;
}

export interface Oid4vcProperty {
    issuerUrl: string;
    clients: Clients;
}

export const DEFAULT_METADATA: IssuerMetadata = {
    credentialIssuer: '',
    authorizationServer: [''],
    credentialOfferEndpoint: '',
    credentialEndpoint: '',
    nonceEndpoint: '',
    deferredCredentialEndpoint: '',
    notificationEndpoint: '',
    credentialRequestEncryption: {
        encValuesSupported: [],
        encryptionRequired: false
    },
    credentialResponseEncryption: {
        algValuesSupported: [],
        encValuesSupported: [],
        encryptionRequired: false
    },
    credentialConfigurationsSupported: {}
};

export const DEFAULT_PROPERTY: Oid4vcProperty = {
    issuerUrl: '',
    clients: {
        clientId: '',
        clientSecret: '',
        redirectUrl: '',
        redirectUris: [],
        scopes: [],
        android: {
            clientId: '',
            redirectUris: [],
            scopes: []
        },
        androidOpenid: {
            clientId: '',
            redirectUris: [],
            scopes: []
        },
        ios: {
            clientId: '',
            redirectUris: [],
            scopes: []
        },
        iosOpenid: {
            clientId: '',
            redirectUris: [],
            scopes: []
        },
        issuerServer: {
            url: ''
        }
    }
};

export const getMetadata = () => getData(API_BASE_URL, 'metadata');
export const updateMetadata = (metadata: IssuerMetadata) => postData(API_BASE_URL, 'metadata', metadata);

export const getCredentialConfigs = () => getData(API_BASE_URL, 'credential-configs');
export const updateCredentialConfigs = (configs: Record<string, SdkCredentialConfig>) => postData(API_BASE_URL, 'credential-configs', configs);

export const getProperties = () => getData(API_BASE_URL, 'properties');
export const updateProperties = (properties: Oid4vcProperty) => postData(API_BASE_URL, 'properties', properties);
