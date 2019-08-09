package com.xitu.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.Item;

@Mapper
@Component
public interface ItemMapper {

	@Select("SELECT * FROM yiyao_item")
	List<Item> getServiceItems();
	
//	@Update("update secret_key set secret_key = #{secretKey.secretKey} where aliyunid = #{secretKey.aliyunid}")
//	void updateItemByService();
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertItem(Item item);
	
	@Delete("delete from yiyao_item where service = #{service}")
    void deleteItemByService(@Param("service") String service);
	
	@Select("select * from yiyao_item where service = #{service} limit 1")
	Item selectItemByService(@Param("service") String service);
}
