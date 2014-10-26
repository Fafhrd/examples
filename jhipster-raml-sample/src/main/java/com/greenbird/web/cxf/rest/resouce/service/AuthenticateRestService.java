package com.greenbird.web.cxf.rest.resouce.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.greenbird.web.cxf.rest.resource.Authenticate;

@Component
public class AuthenticateRestService implements Authenticate {

	private final Logger log = LoggerFactory.getLogger(AuthenticateRestService.class);

	@Context
    private HttpServletRequest servletRequest;
	
	@Override
	public GetAuthenticateResponse getAuthenticate() throws Exception {
        log.debug("REST request to check if the current user is authenticated");
        return GetAuthenticateResponse.withplainOK(servletRequest.getRemoteUser());
	}

}
