package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    //前后有空格
    String INSET_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, name, password, salt, head_url";
//这种 "字符串"+变量 的写法，以后更改起来会很方便，因为数据库可能发生变化，这时只需要改上面String的值即可，比如 INSET_FIELDS 中加入age这样的字符串
    //类似于#{name}这样的写法：是代表底下方法中传入类中的变量，如：int addUser(User user);
    //#{name}就取的是User类中对象的name变量
    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    //一般不这样写：而是像上面那样写
    //@Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,")
    //     values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);
}
