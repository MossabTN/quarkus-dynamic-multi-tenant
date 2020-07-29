package io.maxilog.config;

import io.maxilog.domain.TenantHolder;
import io.quarkus.arc.Unremovable;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
@Unremovable
public class CustomTenantResolver implements TenantResolver {

    @Inject
    TenantHolder tenantHolder;

    @Override
    public String getDefaultTenantId() {
        return "default";
    }

    @Override
    public String resolveTenantId() {
        try {
            return tenantHolder.getTenant();
        } catch (Exception e) {
            return "default";
        }
    }

}