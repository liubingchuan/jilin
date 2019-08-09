package com.xitu.app.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.xitu.app.model.Jiance;

public interface JianceRepository extends ElasticsearchRepository<Jiance,String>{
	Optional<Jiance> findById(String id);
}
