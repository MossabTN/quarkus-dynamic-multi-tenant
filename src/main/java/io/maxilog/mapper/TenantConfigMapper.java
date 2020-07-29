package io.maxilog.mapper;

import io.maxilog.domain.TenantConfig;
import io.maxilog.dto.TenantConfigDTO;
import io.maxilog.dto.TenantOIDCConfigDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {})
public interface TenantConfigMapper {

    TenantOIDCConfigDTO toOidcDto(TenantConfig entity);

    TenantConfigDTO toDto(TenantConfig entity);

    TenantConfig toEntity(TenantConfigDTO dto);

}
