package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Expert;

@Service
public class ExpertService extends AbstractESHttpService {

	@Override
	public Class<Expert> getEntityClass() {
		return Expert.class;
	}
	
}
