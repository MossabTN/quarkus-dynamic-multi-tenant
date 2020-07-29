package io.maxilog.dto;

public class TenantOIDCConfigDTO {

    private String oidcUrl;
    private String oidcRealm;

    public TenantOIDCConfigDTO() {
    }

    public TenantOIDCConfigDTO(String oidcUrl, String oidcRealm) {
        this.oidcUrl = oidcUrl;
        this.oidcRealm = oidcRealm;
    }

    public String getOidcUrl() {
        return oidcUrl;
    }

    public void setOidcUrl(String oidcUrl) {
        this.oidcUrl = oidcUrl;
    }

    public String getOidcRealm() {
        return oidcRealm;
    }

    public void setOidcRealm(String oidcRealm) {
        this.oidcRealm = oidcRealm;
    }
}
