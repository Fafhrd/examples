package com.greenbird.config;

import java.util.Arrays;

import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.greenbird.web.cxf.rest.resouce.service.AccountRestService;
import com.greenbird.web.cxf.rest.resouce.service.ActivateRestService;
import com.greenbird.web.cxf.rest.resouce.service.AuditsRestService;
import com.greenbird.web.cxf.rest.resouce.service.AuthenticateRestService;
import com.greenbird.web.cxf.rest.resouce.service.LogRestService;
import com.greenbird.web.cxf.rest.resouce.service.RegisterRestService;
import com.greenbird.web.cxf.rest.resouce.service.UsersRestService;
import com.greenbird.web.cxf.rest.resource.Account;
import com.greenbird.web.cxf.rest.resource.Activate;
import com.greenbird.web.cxf.rest.resource.Audits;
import com.greenbird.web.cxf.rest.resource.Authenticate;
import com.greenbird.web.cxf.rest.resource.Logs;
import com.greenbird.web.cxf.rest.resource.Register;
import com.greenbird.web.cxf.rest.resource.Users;
import com.greenbird.web.cxf.rest.resouce.service.JaxRsApiApplication;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
@AutoConfigureAfter(WebConfigurer.class)
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class CxfConfigurer {

    private final Logger log = LoggerFactory.getLogger(CxfConfigurer.class);

    @Autowired
    private Bus cxfBus;
    
    @Bean @DependsOn ( "cxf" )
    public Server jaxRsServer() {
        JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance().createEndpoint( jaxRsApiApplication(), JAXRSServerFactoryBean.class );
        
        factory.setServiceBeans( Arrays.< Object >asList( authenticateRestService(), accountRestService(), activateRestService(), auditsRestService(), logRestService(), registerRestService(), usersRestService() ) );
        
        factory.setAddress( factory.getAddress() );
        
        JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();

        ParamConverterProvider paramConverterProvider = new DateParamConverterProvider();

        factory.setProviders( Arrays.< Object >asList( jaxbProvider, paramConverterProvider ) );
        
        Server server = factory.create();
        
        return server;
    }
    
    @Bean 
    public JaxRsApiApplication jaxRsApiApplication() {
        return new JaxRsApiApplication();
    }
 
    @Bean 
    public Activate activateRestService() {
        return new ActivateRestService();
    }  
    
    @Bean 
    public Authenticate authenticateRestService() {
        return new AuthenticateRestService();
    }  

    @Bean 
    public Account accountRestService() {
        return new AccountRestService();
    }  

    @Bean 
    public Audits auditsRestService() {
        return new AuditsRestService();
    }  

    @Bean 
    public Logs logRestService() {
        return new LogRestService();
    }  

    @Bean 
    public Register registerRestService() {
        return new RegisterRestService();
    }  

    @Bean 
    public Users usersRestService() {
        return new UsersRestService();
    }  

    
    
    
    @Bean
    public ServletRegistrationBean cxfServlet() {
        CXFServlet servlet = new CXFServlet();
        return new ServletRegistrationBean(servlet, "/cxf/*");
    }

}
