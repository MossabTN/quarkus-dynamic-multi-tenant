create sequence hibernate_sequence;
create sequence clientidseq start with 3;

create table if not exists client (
    id                  bigint       not null constraint client_pkey primary key,
    datasource_name     varchar(255) not null,
    datasource_password varchar(255) not null,
    datasource_username varchar(255) not null,
    oidc_client         varchar(255) not null,
    oidc_realm          varchar(255) not null,
    oidc_secret         varchar(255) not null,
    tenant_id           varchar(255) not null
);

INSERT INTO public.client (id, datasource_password, datasource_name, datasource_username, oidc_client, oidc_realm, oidc_secret, tenant_id) VALUES (1, 'maxilog-tenant1-password', 'maxilog-tenant1', 'maxilog-tenant1-user', 'back', 'tenant1', 'back', 'tenant1');
INSERT INTO public.client (id, datasource_password, datasource_name, datasource_username, oidc_client, oidc_realm, oidc_secret, tenant_id) VALUES (2, 'maxilog-tenant2-password', 'maxilog-tenant2', 'maxilog-tenant2-user', 'back', 'tenant2', 'back', 'tenant2');
