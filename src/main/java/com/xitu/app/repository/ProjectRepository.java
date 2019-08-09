package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Project;

public interface ProjectRepository extends ElasticsearchRepository<Project,String>{
	Optional<Project> findById(String id);
}
