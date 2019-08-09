package com.xitu.app.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.xitu.app.model.PatentMysql;

@Mapper
@Component
public interface PatentMapper {

	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertPatent(PatentMysql patent);

}
