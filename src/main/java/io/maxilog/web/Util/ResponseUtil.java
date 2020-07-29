package io.maxilog.web.Util;

import javax.ws.rs.core.Response;
import java.util.Optional;

public interface ResponseUtil {


    static <X> Response wrapOrNotFound(Optional<X> maybeResponse) {
        return maybeResponse.map(Response::ok)
            .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }
}
