package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Paper;
import com.xitu.app.model.Patent;

@Service
public class PaperService extends AbstractESHttpService {

	@Override
	public Class<Paper> getEntityClass() {
		return Paper.class;
	}
	
}
