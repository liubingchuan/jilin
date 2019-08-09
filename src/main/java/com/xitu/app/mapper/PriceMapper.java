package com.xitu.app.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.Price;
import com.xitu.app.model.User;

@Mapper
@Component
public interface PriceMapper {

	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertPrice(Price price);
	
	@Select("SELECT * FROM yiyao_price where update_time = #{updateTime} limit 1")
	Price getPriceByUpdateTime(@Param("updateTime") String updateTime);
	
	@Select("SELECT * FROM yiyao_price where name = #{name} order by update_time desc limit 1")
	Price getLatestPrice(@Param("name") String name);
	
	@Select("SELECT * FROM yiyao_price where update_time = #{updateTime}")
	List<Price> getPricesByUpdateTime(@Param("updateTime") String updateTime);
	
	@Select("SELECT name FROM yiyao_price group by name")
	List<String> getPricesGroupByName();
	
	@Select("SELECT avg(avg) FROM yiyao_price where update_time >= #{start} and update_time < #{end} and name = #{name} group by name")
	String getAvgPricesGroupByName(@Param("start") String start, @Param("end") String end, @Param("name") String name);
	
	@Select("SELECT MAX(update_time) FROM yiyao_price;")
	String getLatestUpdateTime();
}
