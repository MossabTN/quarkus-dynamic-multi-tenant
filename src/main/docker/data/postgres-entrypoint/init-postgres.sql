CREATE DATABASE "keycloak";
CREATE USER "maxilog-keycloak-user" WITH PASSWORD 'maxilog-keycloak-password';
GRANT ALL PRIVILEGES ON DATABASE "keycloak" TO "maxilog-keycloak-user";

CREATE DATABASE "maxilog-tenant1";
CREATE USER "maxilog-tenant1-user" WITH PASSWORD 'maxilog-tenant1-password';
GRANT ALL PRIVILEGES ON DATABASE "maxilog-tenant1" TO "maxilog-tenant1-user";


CREATE DATABASE "maxilog-tenant2";
CREATE USER "maxilog-tenant2-user" WITH PASSWORD 'maxilog-tenant2-password';
GRANT ALL PRIVILEGES ON DATABASE "maxilog-tenant2" TO "maxilog-tenant2-user";
