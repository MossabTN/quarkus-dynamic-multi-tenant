package io.maxilog.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.api.AgroalDataSource;
import io.maxilog.config.DatasourceTenantConfigResolver;
import io.maxilog.config.TenantConfigs;
import io.maxilog.dto.TenantConfigDTO;
import io.quarkus.arc.Arc;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.PartialImportRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@Singleton
public class EnvirementService {

    private final DatasourceTenantConfigResolver datasourceTenantConfigResolver;
    private final Flyway flyway;
    private final ObjectMapper mapper;
    @ConfigProperty(name = "maxilog.keycloak.url")
    String host;
    @ConfigProperty(name = "maxilog.keycloak.realm")
    String realm;
    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;
    private Keycloak keycloak;

    @Inject
    public EnvirementService(DatasourceTenantConfigResolver datasourceTenantConfigResolver, Flyway flyway, ObjectMapper mapper) {
        this.datasourceTenantConfigResolver = datasourceTenantConfigResolver;
        this.flyway = flyway;
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder
                .builder()
                .serverUrl(host.concat("/auth")).realm("master")
                .clientId("admin-cli")
                //.clientSecret(clientSecret)
                //.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .username("admin").password("admin").grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    public void createEnv(TenantConfigDTO configDTO) throws Exception {
        createDataBase(configDTO);
        createRealm(configDTO);
    }


    public void checkMigration() {
        ClassicConfiguration configuration = ((ClassicConfiguration) flyway.getConfiguration());
        configuration.setSqlMigrationPrefix("MXV");
        TenantConfigs.getConfigs().forEach(clientConfig -> {
            AgroalDataSource dataSource = datasourceTenantConfigResolver.doCreateDataSource(clientConfig.getTenantId());
            configuration.setDataSource(dataSource);
            Flyway f = new Flyway(configuration);
            f.migrate();
        });

    }

    public void migrate(String tenant) {
        if (!LaunchMode.TEST.getDefaultProfile().equals(ProfileManager.getActiveProfile())) {
            ClassicConfiguration configuration = ((ClassicConfiguration) flyway.getConfiguration());
            configuration.setSqlMigrationPrefix("MXV");
            AgroalDataSource dataSource = datasourceTenantConfigResolver.doCreateDataSource(tenant);
            configuration.setDataSource(dataSource);
            Flyway f = new Flyway(configuration);
            f.migrate();
        }
    }


    private void createDataBase(TenantConfigDTO configDTO) throws SQLException {
        configDTO.setDatasourceName("maxilog-default".replaceAll("default", configDTO.getTenantId()));
        configDTO.setDatasourceUsername("maxilog-default-user".replaceAll("default", configDTO.getTenantId()));
        configDTO.setDatasourcePassword("maxilog-default-password".replaceAll("default", configDTO.getTenantId()));
        try (Connection connection = new QuarkusConnectionProvider(Arc.container().instance(AgroalDataSource.class, new Annotation[0]).get()).getConnection()) {
            Statement statement = connection.createStatement();
            statement.addBatch("CREATE DATABASE \"" + configDTO.getDatasourceName() + "\"");
            statement.addBatch("CREATE USER \"" + configDTO.getDatasourceUsername() + "\" WITH PASSWORD '" + configDTO.getDatasourcePassword() + "'");
            statement.addBatch("GRANT ALL PRIVILEGES ON DATABASE \"" + configDTO.getDatasourceName() + "\" TO \"" + configDTO.getDatasourceUsername() + "\"");
            statement.executeBatch();
        }
    }

    private void createRealm(TenantConfigDTO configDTO) throws IOException {
        configDTO.setOidcRealm(configDTO.getTenantId());
        configDTO.setOidcClient("back");
        configDTO.setOidcSecret("back");

        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(configDTO.getOidcRealm());
        newRealm.setEnabled(true);
        keycloak.realms().create(newRealm);

        RealmRepresentation realmRepresentation = mapper.readValue(new File("../src/main/docker/data/realm/realm.json"), RealmRepresentation.class);
        PartialImportRepresentation partialImport = new PartialImportRepresentation();
        partialImport.setIfResourceExists(PartialImportRepresentation.Policy.SKIP.toString());
        partialImport.setClients(realmRepresentation.getClients());
        partialImport.setGroups(realmRepresentation.getGroups());
        partialImport.setIdentityProviders(realmRepresentation.getIdentityProviders());
        partialImport.setRoles(realmRepresentation.getRoles());
        partialImport.setUsers(realmRepresentation.getUsers());
        keycloak.realm(configDTO.getOidcRealm()).partialImport(partialImport);

    }
}
