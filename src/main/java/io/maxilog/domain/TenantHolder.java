package io.maxilog.domain;

import java.io.Serializable;

public class TenantHolder implements Serializable {

    private String tenant;

    public TenantHolder() {
    }

    public TenantHolder(String tenant) {
        this.tenant = tenant;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
