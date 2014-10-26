package com.greenbird.web.cxf.rest.resouce.service;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.greenbird.domain.Authority;
import com.greenbird.domain.User;
import com.greenbird.repository.UserRepository;
import com.greenbird.security.AuthoritiesConstants;
import com.greenbird.web.cxf.rest.model.UserDTO;
import com.greenbird.web.cxf.rest.resource.Users;

@Component
public class UsersRestService implements Users {

	private final Logger log = LoggerFactory.getLogger(UsersRestService.class);

    @Inject
    private UserRepository userRepository;

	@Override
    @Transactional
    @RolesAllowed(AuthoritiesConstants.ADMIN)
	public GetUsersByLoginResponse getUsersByLogin(String login) throws Exception {
		log.debug("REST request to get User : {}", login);
        return Optional.ofNullable(userRepository.findOne(login))
                .map(user -> GetUsersByLoginResponse.withjsonOK(convert(user)))
                .orElse(GetUsersByLoginResponse.withNotFound());	}

	private UserDTO convert(User user) {
		
		UserDTO userDTO = new UserDTO();
		
		userDTO.setEmail(user.getEmail());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLangKey(user.getLangKey());
		userDTO.setLastName(user.getLastName());
		userDTO.setLogin(user.getLogin());
		userDTO.setPassword(user.getPassword());
		userDTO.setRoles(user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
		
		return userDTO;
	}
	
}
