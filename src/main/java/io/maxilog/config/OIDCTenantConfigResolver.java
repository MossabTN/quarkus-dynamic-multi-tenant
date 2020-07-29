package io.maxilog.config;

import io.maxilog.domain.TenantConfig;
import io.maxilog.web.errors.NotFoundException;
import io.quarkus.oidc.OidcTenantConfig;
import io.quarkus.oidc.TenantConfigResolver;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

@ApplicationScoped
public class OIDCTenantConfigResolver implements TenantConfigResolver {


    private final Config configuration;

    @Inject
    public OIDCTenantConfigResolver(Config configuration) {
        this.configuration = configuration;
    }

    @Override
    public OidcTenantConfig resolve(RoutingContext context) {

        try {
            String tenant = context.request().headers().get("x-tenant");

            if (Objects.equals(tenant, "default")) {
                return getDefaultOidcTenantConfig();
            }
            TenantConfig tenantConfig = TenantConfigs.findOptionalClient(tenant).orElseThrow(() -> new NotFoundException("No oidc config for this client"));

            OidcTenantConfig config = new OidcTenantConfig();
            config.setTenantId(tenant);
            config.setAuthServerUrl(configuration.getValue("maxilog.keycloak.url", String.class) + "/auth/realms/" + tenantConfig.getOidcRealm());
            config.setClientId(tenantConfig.getOidcClient());
            OidcTenantConfig.Credentials credentials = new OidcTenantConfig.Credentials();
            credentials.setSecret(tenantConfig.getOidcSecret());
            config.setCredentials(credentials);

            return config;
        } catch (Exception e) {
            return getDefaultOidcTenantConfig();
        }

    }

    private OidcTenantConfig getDefaultOidcTenantConfig() {
        OidcTenantConfig config = new OidcTenantConfig();
        config.setTenantId("default");
        config.setAuthServerUrl(configuration.getValue("quarkus.oidc.auth-server-url", String.class));
        config.setClientId(configuration.getValue("quarkus.oidc.client-id", String.class));
        OidcTenantConfig.Credentials credentials = new OidcTenantConfig.Credentials();
        credentials.setSecret(configuration.getValue("quarkus.oidc.credentials.secret", String.class));
        config.setCredentials(credentials);
        return config;
    }
}