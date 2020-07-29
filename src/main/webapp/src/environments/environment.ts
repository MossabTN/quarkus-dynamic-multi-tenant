// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The getMyTasks of which env maps to which file can be found in `.angular-cli.json`.

import {KeycloakConfig} from 'keycloak-angular';

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