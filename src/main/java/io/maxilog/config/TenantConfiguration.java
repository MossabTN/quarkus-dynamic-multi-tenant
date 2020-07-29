package io.maxilog.config;

import io.maxilog.domain.TenantHolder;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;

public class TenantConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantConfiguration.class);

    @RequestScoped
    @Produces
    public TenantHolder tenantHolder(RoutingContext context) {
        try {
            return Optional.ofNullable(context.request().headers().get("x-tenant"))
                    .map(TenantHolder::new)
                    .orElse(new TenantHolder("default"));
        } catch (Exception e) {
            return new TenantHolder("default");
        }
    }
}
