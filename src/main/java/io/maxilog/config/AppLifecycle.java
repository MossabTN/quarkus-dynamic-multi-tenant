package io.maxilog.config;

import io.maxilog.repository.TenantConfigRepository;
import io.maxilog.service.impl.EnvirementService;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class AppLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycle.class);
    private final TenantConfigRepository tenantConfigRepository;
    private final EnvirementService multiTenantService;


    @Inject
    public AppLifecycle(TenantConfigRepository tenantConfigRepository, EnvirementService multiTenantService) {
        this.tenantConfigRepository = tenantConfigRepository;
        this.multiTenantService = multiTenantService;
    }

    void onStart(@Observes StartupEvent ev) {
        TenantConfigs.setConfigs(tenantConfigRepository.findAll());
        multiTenantService.checkMigration();

    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}
