package io.maxilog.config;

import io.maxilog.domain.TenantConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TenantConfigs {

    private static List<TenantConfig> configs = new ArrayList<>();

    public static List<TenantConfig> getConfigs() {
        return configs;
    }

    public static void setConfigs(List<TenantConfig> configs) {
        TenantConfigs.configs = configs;
    }

    public static TenantConfig findClient(String tenant) {
        return configs.stream().filter(config -> config.getTenantId().equals(tenant)).findFirst().orElse(null);
    }

    public static Optional<TenantConfig> findOptionalClient(String tenant) {
        return configs.stream().filter(config -> config.getTenantId().equals(tenant)).findFirst();
    }

    public static void addClient(TenantConfig config) {
        configs.add(config);
    }
}