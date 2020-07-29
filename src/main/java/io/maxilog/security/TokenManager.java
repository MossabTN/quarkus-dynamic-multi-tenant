package io.maxilog.security;

import io.maxilog.config.TenantConfigs;
import io.maxilog.domain.TenantConfig;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;

import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Startup
@Singleton
public class TokenManager {

    @ConfigProperty(name = "maxilog.keycloak.url")
    String host;
    @ConfigProperty(name = "maxilog.keycloak.realm")
    String realm;
    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;
    private Map<String, Keycloak> currentTokens = new HashMap<>();
    private Map<String, Long> expirationTimes = new HashMap<>();

    public synchronized AccessTokenResponse getAccessToken(String tenant) {
        if (currentTokens.get(tenant) == null) {
            grantToken(tenant);
        } else if (tokenExpired(tenant)) {
            refreshToken(tenant);
        }
        return currentTokens.get(tenant).tokenManager().getAccessToken();
    }

    private AccessTokenResponse grantToken(String tenant) {

        Keycloak keycloak;
        if (tenant == null || Objects.equals(tenant, "default")) {
            keycloak = KeycloakBuilder
                    .builder()
                    .serverUrl(host.concat("/auth")).realm(this.realm)
                    .clientId(clientId).clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
        } else {
            TenantConfig tenantConfig = TenantConfigs.findOptionalClient(tenant).orElseThrow(() -> new NotFoundException("No config for this client"));
            keycloak = KeycloakBuilder
                    .builder()
                    .serverUrl(host.concat("/auth")).realm(tenantConfig.getOidcRealm())
                    .clientId(tenantConfig.getOidcClient()).clientSecret(tenantConfig.getOidcSecret())
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
        }
        long requestTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        synchronized (TokenManager.class) {
            AccessTokenResponse currentToken = keycloak.tokenManager().grantToken();
            currentTokens.put(tenant, keycloak);
            expirationTimes.put(tenant, requestTime + currentToken.getExpiresIn());
        }
        return currentTokens.get(tenant).tokenManager().getAccessToken();
    }

    private synchronized AccessTokenResponse refreshToken(String tenant) {
        try {
            long requestTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            AccessTokenResponse currentToken = this.currentTokens.get(tenant).tokenManager().refreshToken();
            expirationTimes.put(tenant, requestTime + currentToken.getExpiresIn());
            return currentToken;
        } catch (WebApplicationException e) {
            return grantToken(tenant);
        }
    }

    private synchronized boolean tokenExpired(String tenant) {
        long minTokenValidity = 30L;
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() + minTokenValidity >= expirationTimes.getOrDefault(tenant, 0L);
    }

}
