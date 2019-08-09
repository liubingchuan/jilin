package com.xitu.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import com.xitu.app.model.User;

@Mapper
@Component
public interface UserMapper {

	@Select("SELECT * FROM yiyao_user where account = #{account} limit 1")
	User getUserByAccount(@Param("account") String account);
	
	@Select("SELECT * FROM yiyao_user where open_id = #{openId} limit 1")
	User getUserByOpenId(@Param("openId") String openId);
	
	@Select("select * from yiyao_user limit #{pageIndex}, #{pageSize}")
    List<User> getUsers(@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
	
	@Select("select count(*) from yiyao_user")
    int getUserCount();
	
	@InsertProvider(type = BasedProvider.class, method = BasedProvider.INSERT)
	int insertUser(User user);
	
	@Update("UPDATE yiyao_user SET account = #{user.account}, password = #{user.password}, name = #{user.name}, "
			    + "identity = #{user.identity}, unit = #{user.unit}, job = #{user.job}, "
			    + "duty = #{user.duty}, major = #{user.major}, role = #{user.role}, email = #{user.email}, "
			    + "phone = #{user.phone}, stamp = #{user.stamp} "
		        + "WHERE id = #{user.id}")
	void updateById(@Param("user") User user);
	
	@Update("UPDATE yiyao_user SET account = #{user.account}, password = #{user.password}, email = #{user.email} "
			+ "WHERE open_id = #{user.openId}")
	void updateByOpenId(@Param("user") User user);
	
}
