package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Jiance;

@Service
public class JianceService extends AbstractESHttpService {

	@Override
	public Class<Jiance> getEntityClass() {
		return Jiance.class;
	}
	
}
