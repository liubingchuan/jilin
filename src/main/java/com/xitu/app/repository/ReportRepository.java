package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Report;

public interface ReportRepository extends ElasticsearchRepository<Report,String>{
	Optional<Report> findById(String id);
}
