package com.greenbird.web.cxf.rest.resouce.service;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.greenbird.domain.Authority;
import com.greenbird.domain.User;
import com.greenbird.repository.PersistentTokenRepository;
import com.greenbird.repository.UserRepository;
import com.greenbird.security.SecurityUtils;
import com.greenbird.service.UserService;
import com.greenbird.web.cxf.rest.model.PersistentToken;
import com.greenbird.web.cxf.rest.model.UserDTO;
import com.greenbird.web.cxf.rest.resource.Account;

@Component
public class AccountRestService implements Account {

	private final Logger log = LoggerFactory.getLogger(AccountRestService.class);

	@Context
    private HttpServletRequest servletRequest;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;
    
    @Inject
    private PersistentTokenRepository persistentTokenRepository;
    
	@Override
	public GetAccountResponse getAccount() throws Exception {
		log.debug("getAccount");
        return (GetAccountResponse) Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> Account.GetAccountResponse.withjsonOK(convert(user)))
            .orElse( Account.GetAccountResponse.withInternalServerError());
	}

	@Override
	public PostAccountResponse postAccount(UserDTO userDTO) throws Exception {
		log.debug("postAccount");

        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());

        return PostAccountResponse.withOK();
	}

	@Override
	public PostAccountChangePasswordResponse postAccountChangePassword(Reader reader) throws Exception {
		log.debug("postAccountChangePassword");

		StringWriter writer = new StringWriter();
		char[] chars = new char[1024];
		int read;
		do {
			read = reader.read(chars);
			if(read == -1) {
				break;
			}
			writer.write(chars, 0, read);
		} while(true);

		String password = writer.toString();
		
        if (StringUtils.isEmpty(password)) {
            return PostAccountChangePasswordResponse.withForbidden();
        }
        userService.changePassword(password);
        return PostAccountChangePasswordResponse.withOK();
	}

	@Override
	public GetAccountSessionsResponse getAccountSessions() throws Exception {
		log.debug("getAccountSessions");

		return (GetAccountSessionsResponse) Optional.ofNullable(userRepository.findOne(SecurityUtils.getCurrentLogin()))
	            .map(user -> GetAccountSessionsResponse.withjsonOK(convert(persistentTokenRepository.findByUser(user))))
	            .orElse(Account.GetAccountSessionsResponse.withInternalServerError());
		
	}

	private List<PersistentToken> convert(List<com.greenbird.domain.PersistentToken> tokens) {
		
		List<PersistentToken> list = new ArrayList<PersistentToken>();
		
		for(com.greenbird.domain.PersistentToken pt : tokens) {
			PersistentToken token = new PersistentToken();
			
			token.setUserAgent(pt.getUserAgent());
			token.setSeries(pt.getSeries());
			token.setIpAddress(pt.getIpAddress());
			token.setFormattedTokenDate(pt.getFormattedTokenDate());
			
			list.add(token);
		}
		
		return list;
	}

	@Override
	public DeleteAccountSessionsBySeriesResponse deleteAccountSessionsBySeries(
			String series) throws Exception {
		
		log.debug("deleteAccountSessionsBySeries");

		String decodedSeries = URLDecoder.decode(series, "UTF-8");
        User user = userRepository.findOne(SecurityUtils.getCurrentLogin());
        if (persistentTokenRepository.findByUser(user).stream()
                .filter(persistentToken -> StringUtils.equals(persistentToken.getSeries(), decodedSeries))
                .count() > 0) {

            persistentTokenRepository.delete(decodedSeries);
        }
		return DeleteAccountSessionsBySeriesResponse.withOK();
	}
	
	private UserDTO convert(User user) {
		
		UserDTO userDTO = new UserDTO();
		
		userDTO.setEmail(user.getEmail());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLangKey(user.getLangKey());
		userDTO.setLastName(user.getLastName());
		userDTO.setLogin(user.getLogin());
		userDTO.setRoles(user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
		
		return userDTO;
	}
}
