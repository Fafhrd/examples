package com.greenbird.web.cxf.rest.resouce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.greenbird.web.cxf.rest.model.LoggerDTO;
import com.greenbird.web.cxf.rest.resource.Logs;

@Component
public class LogRestService implements Logs {

	private final Logger log = LoggerFactory.getLogger(LogRestService.class);

	@Override
	public GetLogsResponse getLogs() throws Exception {
		log.debug("getLogs");
		
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<com.greenbird.web.rest.dto.LoggerDTO> collect = context.getLoggerList()
            .stream()
            .map(com.greenbird.web.rest.dto.LoggerDTO::new)
            .collect(Collectors.toList());
        
        return GetLogsResponse.withjsonOK(convert(collect));
		
	}
	
	private List<LoggerDTO> convert(List<com.greenbird.web.rest.dto.LoggerDTO> tokens) {
		List<LoggerDTO> list = new ArrayList<LoggerDTO>();
		
		for(com.greenbird.web.rest.dto.LoggerDTO pt : tokens) {
			LoggerDTO token = new LoggerDTO();

			token.setLevel(pt.getLevel());
			token.setName(pt.getName());
			
			list.add(token);
		}
		
		return list;
	}

	@Override
	public PutLogsResponse putLogs(LoggerDTO jsonLogger) throws Exception {
		log.debug("putLogs");
		
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
        
        return PutLogsResponse.withOK();
	}

	
}
