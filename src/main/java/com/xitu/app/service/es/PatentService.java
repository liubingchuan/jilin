package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Patent;

@Service
public class PatentService extends AbstractESHttpService {

	@Override
	public Class<Patent> getEntityClass() {
		return Patent.class;
	}
	
}
