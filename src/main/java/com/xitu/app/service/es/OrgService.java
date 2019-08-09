package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Org;

@Service
public class OrgService extends AbstractESHttpService {

	@Override
	public Class<Org> getEntityClass() {
		return Org.class;
	}
	
}
