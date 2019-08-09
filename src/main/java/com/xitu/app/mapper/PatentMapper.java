package com.xitu.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.PatentMysql;

@Mapper
@Component
public interface PatentMapper {

	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertPatent(PatentMysql patent);
	
	@Select("select * from jilin_patent order by id asc limit #{index}, #{pageSize}")
    List<PatentMysql> getPatents(@Param("index") int index, @Param("pageSize") int pageSize);

}
