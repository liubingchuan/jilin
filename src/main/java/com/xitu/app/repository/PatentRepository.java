package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Paper;
import com.xitu.app.model.Patent;
import com.xitu.app.model.Project;

public interface PatentRepository extends ElasticsearchRepository<Patent,String>{
	Optional<Patent> findById(String id);
}
