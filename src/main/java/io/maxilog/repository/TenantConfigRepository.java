package io.maxilog.repository;

import io.maxilog.domain.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {

    Optional<TenantConfig> findByTenantId(String tenant);


}
