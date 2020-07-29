package io.maxilog.domain;

import javax.persistence.*;

@Entity
@Table(name = "client")
public class TenantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "tenant_id")
    private String tenantId;

    /*
    @Column(nullable = false, name = "datasource_host")
    private String datasourceHost;
    */

    @Column(nullable = false, name = "datasource_name")
    private String datasourceName;

    @Column(nullable = false, name = "datasource_username")
    private String datasourceUsername;

    @Column(nullable = false, name = "datasource_password")
    private String datasourcePassword;

    /*
    @Column(nullable = false, name = "oidc_url")
    private String oidcUrl;
    */

    @Column(nullable = false, name = "oidc_realm")
    private String oidcRealm;

    @Column(nullable = false, name = "oidc_client")
    private String oidcClient;

    @Column(nullable = false, name = "oidc_secret")
    private String oidcSecret;

    public TenantConfig() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
