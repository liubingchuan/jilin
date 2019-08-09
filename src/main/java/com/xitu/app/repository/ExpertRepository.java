package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Expert;
import com.xitu.app.model.Org;

public interface ExpertRepository extends ElasticsearchRepository<Expert,String>{
	Optional<Expert> findById(String id);
}
