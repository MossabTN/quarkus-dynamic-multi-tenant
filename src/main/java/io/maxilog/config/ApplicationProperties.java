package io.maxilog.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ConfigProperties(prefix = "maxilog")
public interface ApplicationProperties {

    @ConfigProperty(name = "application.name")
    String applicationName();

    @ConfigProperty(name = "keycloak.url")
    String keycloakUrl();

    @ConfigProperty(name = "keycloak.realm")
    String keycloakRealm();

    @ConfigProperty(name = "datasource.host")
    String datasourceHost();

    @ConfigProperty(name = "datasource.db")
    String datasourceDatabaseName();

}