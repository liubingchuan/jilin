package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Paper;
import com.xitu.app.model.Patent;
import com.xitu.app.model.Project;

@Service
public class ProjectService extends AbstractESHttpService {

	@Override
	public Class<Project> getEntityClass() {
		return Project.class;
	}
	
}
