package com.greenbird.web.cxf.rest.resouce.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.xml.ws.spi.http.HttpContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import com.greenbird.domain.User;
import com.greenbird.repository.UserRepository;
import com.greenbird.service.MailService;
import com.greenbird.service.UserService;
import com.greenbird.web.cxf.rest.model.UserDTO;
import com.greenbird.web.cxf.rest.resource.Authenticate;
import com.greenbird.web.cxf.rest.resource.Register;

@Component
public class RegisterRestService implements Register {

	private final Logger log = LoggerFactory.getLogger(RegisterRestService.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    @Inject
    private SpringTemplateEngine templateEngine;

	@Context
    private HttpServletRequest servletRequest;

	@Context
    private HttpServletResponse servletResponse;

    @Inject
    private ServletContext servletContext;

    @Inject
    private ApplicationContext applicationContext;

	@Override
	public PostRegisterResponse postRegister(UserDTO userDTO) throws Exception {
		
        return Optional.ofNullable(userRepository.findOne(userDTO.getLogin()))
                .map(user -> PostRegisterResponse.withNotModified())
                .orElseGet(() -> {
                    User user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                            userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
                            userDTO.getLangKey());
                    final Locale locale = Locale.forLanguageTag(user.getLangKey());
                    String content = createHtmlContentFromTemplate(user, locale, servletRequest, servletResponse);
                    mailService.sendActivationEmail(user.getEmail(), content, locale);
                    return PostRegisterResponse.withCreated();});
	}


    private String createHtmlContentFromTemplate(final User user, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
                                 request.getServerName() +       // "myhost"
                                 ":" + request.getServerPort());
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process("activationEmail", context);
    }

}
