package com.greenbird.web.cxf.rest.resouce.service;

import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.greenbird.service.UserService;
import com.greenbird.web.cxf.rest.resource.Activate;
import com.greenbird.web.cxf.rest.resource.Authenticate;

@Component
public class ActivateRestService implements Activate {

	private final Logger log = LoggerFactory.getLogger(ActivateRestService.class);

    @Inject
    private UserService userService;
    
	@Override
	public GetActivateResponse getActivate(String key) throws Exception {
		log.debug("getActivate");
        return Optional.ofNullable(userService.activateRegistration(key))
                .map(user -> GetActivateResponse.withplainOK(user.getLogin()))
                .orElse(GetActivateResponse.withInternalServerError());
	}

	

}
