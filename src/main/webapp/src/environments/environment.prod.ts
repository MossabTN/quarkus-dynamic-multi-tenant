import {KeycloakConfig} from 'keycloak-angular';

// Add here your keycloak setup infos
let keycloakConfig: KeycloakConfig = {
    url: 'http://localhost:8180/auth',
    realm: 'default',
    clientId: 'front'
};

export const environment = {
    production: false,
    apis: {
        backend: 'http://localhost:8080'
    },
    keycloak: keycloakConfig
};