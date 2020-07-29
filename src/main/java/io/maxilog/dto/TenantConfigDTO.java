package io.maxilog.dto;

public class TenantConfigDTO {

    private String tenantId;
    private String datasourceName;
    private String datasourceUsername;
    private String datasourcePassword;
    private String oidcRealm;
    private String oidcClient;
    private String oidcSecret;

    public TenantConfigDTO() {
    }

    public TenantConfigDTO(String tenantId) {
        this.tenantId = tenantId;
    }

    public TenantConfigDTO(String tenantId, String datasourceName, String datasourceUsername, String datasourcePassword, String oidcRealm, String oidcClient, String oidcSecret) {
        this.tenantId = tenantId;
        this.datasourceName = datasourceName;
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
        this.oidcRealm = oidcRealm;
        this.oidcClient = oidcClient;
        this.oidcSecret = oidcSecret;
    }


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDatasourceUsername() {
        return datasourceUsername;
    }

    public void setDatasourceUsername(String datasourceUsername) {
        this.datasourceUsername = datasourceUsername;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public void setDatasourcePassword(String datasourcePassword) {
        this.datasourcePassword = datasourcePassword;
    }

    public String getOidcRealm() {
        return oidcRealm;
    }

    public void setOidcRealm(String oidcRealm) {
        this.oidcRealm = oidcRealm;
    }

    public String getOidcClient() {
        return oidcClient;
    }

    public void setOidcClient(String oidcClient) {
        this.oidcClient = oidcClient;
    }

    public String getOidcSecret() {
        return oidcSecret;
    }

    public void setOidcSecret(String oidcSecret) {
        this.oidcSecret = oidcSecret;
    }

}
