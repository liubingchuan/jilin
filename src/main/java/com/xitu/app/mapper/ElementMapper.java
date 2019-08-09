package com.xitu.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.ElementMaster;
import com.xitu.app.model.ElementSlave;

@Mapper
@Component
public interface ElementMapper {

//	@Select("SELECT * FROM xitu_item")
//	List<Item> getServiceItems();
	
//	@Update("update secret_key set secret_key = #{secretKey.secretKey} where aliyunid = #{secretKey.aliyunid}")
//	void updateItemByService();
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertMaster(ElementMaster master);
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertSlave(ElementSlave slave);
	
//	@Delete("delete from xitu_item where service = #{service}")
//    void deleteItemByService(@Param("service") String service);
//	
	@Select("select * from element_master where name = #{name} limit 1")
	ElementMaster selectMasterByName(@Param("name") String name);
	
	@Select("select * from element_master")
	List<ElementMaster> selectAllMasters();
	
	@Select("select * from element_slave")
	List<ElementSlave> selectAllSlaves();
	
	@Select("select * from element_slave where name = #{name} limit 1")
	ElementSlave selectSlaveByName(@Param("name") String name);
	
	@Select("select * from element_slave where master = #{master}")
	List<ElementSlave> selectSlavesByMaster(@Param("master") String master);
}

