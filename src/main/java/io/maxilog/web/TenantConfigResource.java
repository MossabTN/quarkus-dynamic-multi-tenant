package io.maxilog.web;


import io.maxilog.dto.TenantConfigDTO;
import io.maxilog.service.impl.TenantConfigService;
import io.maxilog.web.Util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Path("/api/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantConfigResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantConfigResource.class);

    private final TenantConfigService tenantConfigService;

    @Inject
    public TenantConfigResource(TenantConfigService tenantConfigService) {
        this.tenantConfigService = tenantConfigService;
    }

    @GET
    @Path("/{tenant}/config")
    public Response findById(@PathParam("tenant") String tenant) {
        LOGGER.debug("REST request to get tenant Config by tenantId : {}", tenant);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tenantConfigService.findById(tenant)));
    }

    @POST
    @Path("/{tenant}")
    public Response addClient(@PathParam("tenant") String tenant) throws URISyntaxException {
        LOGGER.debug("REST request to save tenant : {}", tenant);
        tenantConfigService.save(new TenantConfigDTO(tenant));
        return Response
                .created(new URI("/tenants/" + tenant + "/config"))
                .build();
    }
}
