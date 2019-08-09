package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Paper;
import com.xitu.app.model.Project;

public interface PaperRepository extends ElasticsearchRepository<Paper,String>{
	Optional<Paper> findById(String id);
}
