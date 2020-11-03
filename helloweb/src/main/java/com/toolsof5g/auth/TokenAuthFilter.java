package com.toolsof5g.auth;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import com.toolsof5g.interfaces.TokenAuth;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Priority(Priorities.AUTHENTICATION)
@Provider
@TokenAuth
public class TokenAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        String token = req.getHeaderString("Token");

        if (!"admin".equals(token)) {
            Response resp = Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("no permission.")
                    .build();
            req.abortWith(resp);
        }
    }

}