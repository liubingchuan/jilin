package com.xitu.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.xitu.app.model.Task;

@Mapper
@Component
public interface TaskMapper {

	@Select("SELECT * FROM yiyao_task where id = #{id} limit 1")
	Task getTaskById(@Param("id") Integer id);
	
//	@Select("select * from yiyao_user limit #{pageIndex}, #{pageSize}")
//    List<User> getUsers(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
//	
//	@Select("select count(*) from yiyao_user")
//    int getUserCount();
//	
//	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
//	int insertUser(User user);
//	
//	@Update("UPDATE yiyao_user SET account = #{user.account}, password = #{user.password}, name = #{user.name}, "
//			    + "identity = #{user.identity}, unit = #{user.unit}, job = #{user.job}, "
//			    + "duty = #{user.duty}, major = #{user.major}, email = #{user.email}, "
//			    + "phone = #{user.phone}, stamp = #{user.stamp} "
//		        + "WHERE id = #{user.id}")
//	void updateById(@Param("user") User user);

}
