package com.greenbird.web.cxf.rest.resouce.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.greenbird.security.AuthoritiesConstants;
import com.greenbird.service.AuditEventService;
import com.greenbird.web.cxf.rest.model.AuditEvent;
import com.greenbird.web.cxf.rest.model.Data;
import com.greenbird.web.cxf.rest.resource.Audits;

@Component
public class AuditsRestService implements Audits {

	private final Logger log = LoggerFactory.getLogger(AuditsRestService.class);

	@Inject
    private AuditEventService auditEventService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	log.debug("initBinder");
    	
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));

    }

	    
	@Override
    @RolesAllowed(AuthoritiesConstants.ADMIN)
	public GetAuditsAllResponse getAuditsAll() throws Exception {
		return GetAuditsAllResponse.withjsonOK(convert(auditEventService.findAll()));
	}

	private List<AuditEvent> convert(List<org.springframework.boot.actuate.audit.AuditEvent> tokens) {
		List<AuditEvent> list = new ArrayList<AuditEvent>();
		
		for(org.springframework.boot.actuate.audit.AuditEvent ae : tokens) {
			AuditEvent token = new AuditEvent();
			
			Data tokenData = new Data();
			tokenData.getAdditionalProperties().putAll(ae.getData());
			token.setData(tokenData);
			
			token.setPrincipal(ae.getPrincipal());
			token.setTimestamp(ae.getTimestamp());
			token.setType(ae.getType());
			
			list.add(token);
		}
		
		return list;	}


	@Override
    @RolesAllowed(AuthoritiesConstants.ADMIN)
	public GetAuditsByDatesResponse getAuditsByDates(Date fromDate, Date toDate)
			throws Exception {
		return GetAuditsByDatesResponse.withjsonOK(convert(auditEventService.findByDates(new LocalDateTime(fromDate), new LocalDateTime(toDate))));
	}

	

}
