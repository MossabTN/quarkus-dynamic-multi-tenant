package io.maxilog.service.impl;

import io.maxilog.config.ApplicationProperties;
import io.maxilog.config.TenantConfigs;
import io.maxilog.domain.TenantConfig;
import io.maxilog.dto.TenantConfigDTO;
import io.maxilog.dto.TenantOIDCConfigDTO;
import io.maxilog.mapper.TenantConfigMapper;
import io.maxilog.repository.TenantConfigRepository;
import io.maxilog.web.errors.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;

@Singleton
public class TenantConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantConfigService.class);

    private final EnvirementService multiTenantService;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantConfigMapper tenantConfigMapper;
    private final ApplicationProperties configuration;


    @Inject
    public TenantConfigService(EnvirementService multiTenantService, TenantConfigRepository tenantConfigRepository, TenantConfigMapper tenantConfigMapper, ApplicationProperties configuration) {
        this.multiTenantService = multiTenantService;
        this.tenantConfigRepository = tenantConfigRepository;
        this.tenantConfigMapper = tenantConfigMapper;
        this.configuration = configuration;
    }

    public TenantOIDCConfigDTO findById(String tenant) {
        return tenantConfigRepository.findByTenantId(tenant)
                .map(clientConfig -> new TenantOIDCConfigDTO(configuration.keycloakUrl() + "/auth/", clientConfig.getOidcRealm()))
                .orElseThrow(() -> new NotFoundException("Client Config Not Found"));
    }

    public void save(TenantConfigDTO tenantConfigDTO) {
        try {
            multiTenantService.createEnv(tenantConfigDTO);
            TenantConfig config = tenantConfigMapper.toEntity(tenantConfigDTO);
            tenantConfigRepository.save(config);
            TenantConfigs.addClient(config);
            multiTenantService.migrate(config.getTenantId());
        } catch (Exception e) {
            //TODO delete database and realm
            e.printStackTrace();
            throw new WebApplicationException("Error while creating new tenant");
        }

    }

}
