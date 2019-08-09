package com.xitu.app.service.es;

import org.springframework.stereotype.Service;

import com.xitu.app.model.Report;

@Service
public class ReportService extends AbstractESHttpService {

	@Override
	public Class<Report> getEntityClass() {
		return Report.class;
	}
	
}
