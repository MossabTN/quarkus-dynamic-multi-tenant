package io.maxilog.web;

import io.maxilog.dto.UserDTO;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

    private final SecurityIdentity identity;

    public UsersResource(SecurityIdentity identity) {
        this.identity = identity;
    }

    @GET
    @Path("/me")
    @NoCache
    public UserDTO me() {
        return new UserDTO(((OidcJwtCallerPrincipal)identity.getPrincipal()).getClaims());
    }

}
