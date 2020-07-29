package io.maxilog.config;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.AgroalPoolInterceptor;
import io.agroal.api.configuration.AgroalConnectionPoolConfiguration;
import io.agroal.api.configuration.AgroalDataSourceConfiguration;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import io.agroal.narayana.NarayanaTransactionIntegration;
import io.agroal.pool.DataSource;
import io.maxilog.domain.TenantConfig;
import io.quarkus.agroal.runtime.AgroalConnectionConfigurer;
import io.quarkus.agroal.runtime.JdbcDriver;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.Unremovable;
import io.quarkus.datasource.common.runtime.DataSourceUtil;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.hibernate.orm.runtime.tenant.TenantConnectionResolver;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Alternative()
@Priority(1)
@Unremovable
@ApplicationScoped
@Transactional
public class DatasourceTenantConfigResolver implements TenantConnectionResolver {

    private static final Logger LOG = Logger.getLogger(DatasourceTenantConfigResolver.class);


    private final TransactionManager transactionManager;
    private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    private final Instance<AgroalPoolInterceptor> agroalPoolInterceptors;
    private final ApplicationProperties configuration;

    @Inject
    public DatasourceTenantConfigResolver(TransactionManager transactionManager, TransactionSynchronizationRegistry transactionSynchronizationRegistry, Instance<AgroalPoolInterceptor> agroalPoolInterceptors, ApplicationProperties configuration) {
        this.transactionManager = transactionManager;
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
        this.agroalPoolInterceptors = agroalPoolInterceptors;
        this.configuration = configuration;
    }

    @Override
    public ConnectionProvider resolve(String tenantId) {
        LOG.debugv("resolve({0})", tenantId);
        if (!Objects.equals(tenantId, "default")) {
            return new QuarkusConnectionProvider(doCreateDataSource(tenantId));
        }
        return new QuarkusConnectionProvider(Arc.container().instance(AgroalDataSource.class, new Annotation[0]).get());
    }

    public AgroalDataSource doCreateDataSource(String tenant) {

        TenantConfig tenantConfig = TenantConfigs.findOptionalClient(tenant).orElseThrow(() -> new NotFoundException("No config for this client"));

        if (tenantConfig.getDatasourceName() == null) {
            //throw new ConfigurationException("URL is not defined for datasource " + tenant);
            throw new NotFoundException("URL is not defined for datasource " + tenant);
        }

        String resolvedDriverClass = "org.postgresql.Driver";

        Class driver;
        try {
            driver = Class.forName(resolvedDriverClass, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException var21) {
            throw new RuntimeException("Unable to load the datasource driver " + resolvedDriverClass + " for datasource " + tenant, var21);
        }

        String resolvedDbKind = "postgresql";
        InstanceHandle<AgroalConnectionConfigurer> agroalConnectionConfigurerHandle = Arc.container().instance(AgroalConnectionConfigurer.class, new JdbcDriver.JdbcDriverLiteral(resolvedDbKind));
        AgroalDataSourceConfigurationSupplier dataSourceConfiguration = new AgroalDataSourceConfigurationSupplier();

        AgroalConnectionPoolConfigurationSupplier poolConfiguration = dataSourceConfiguration.connectionPoolConfiguration();
        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfiguration = poolConfiguration.connectionFactoryConfiguration();
        boolean mpMetricsPresent = false;


        connectionFactoryConfiguration.jdbcUrl(configuration.datasourceHost() + tenantConfig.getDatasourceName());
        connectionFactoryConfiguration.connectionProviderClass(driver);
        connectionFactoryConfiguration.trackJdbcResources(true);

        io.agroal.api.transaction.TransactionIntegration txIntegration = new NarayanaTransactionIntegration(this.transactionManager, this.transactionSynchronizationRegistry);
        poolConfiguration.transactionIntegration(txIntegration);

        dataSourceConfiguration.metricsEnabled(mpMetricsPresent);


        connectionFactoryConfiguration.principal(new NamePrincipal(tenantConfig.getDatasourceUsername()));

        connectionFactoryConfiguration.credential(new SimplePassword(tenantConfig.getDatasourcePassword()));

        final String validationQuery;

        poolConfiguration.minSize(4);
        poolConfiguration.maxSize(20);

        poolConfiguration.connectionValidator(AgroalConnectionPoolConfiguration.ConnectionValidator.defaultValidator());
        poolConfiguration.acquisitionTimeout(Duration.ofSeconds(5));
        poolConfiguration.validationTimeout(Duration.ofSeconds(120));

        poolConfiguration.reapTimeout(Duration.ofSeconds(300));


        if (agroalConnectionConfigurerHandle.isAvailable()) {
            agroalConnectionConfigurerHandle.get().disableSslSupport(resolvedDbKind, dataSourceConfiguration);
        } else {
            LOG.warnv("Agroal does not support disabling SSL for database kind: {0}", resolvedDbKind);
        }

        AgroalDataSourceConfiguration agroalConfiguration = dataSourceConfiguration.get();
        AgroalDataSource dataSource = new DataSource(agroalConfiguration, new AgroalEventLoggingListener(tenant));
        LOG.debugv("Started datasource {0} connected to {1}", tenant, agroalConfiguration.connectionPoolConfiguration().connectionFactoryConfiguration().jdbcUrl());
        Collection<AgroalPoolInterceptor> interceptorList = this.agroalPoolInterceptors.select(new Annotation[]{tenant != null && !DataSourceUtil.isDefault(tenant) ? new io.quarkus.agroal.DataSource.DataSourceLiteral(tenant) : Default.Literal.INSTANCE}).stream().collect(Collectors.toList());
        if (!interceptorList.isEmpty()) {
            dataSource.setPoolInterceptors(interceptorList);
        }

        return dataSource;
    }
}