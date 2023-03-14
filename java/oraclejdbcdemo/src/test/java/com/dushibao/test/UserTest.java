package com.dushibao.test;

import com.dushibao.dao.UserDao;
import com.dushibao.model.User;
import com.dushibao.utils.DBUtils;
import org.junit.Test;

import java.util.Date;

public class UserTest {

    /**
     * TNS:no appropriate service handler found
     * 数据库 连接数超了
     */
    @Test
    public void add(){
        UserDao userDao = new UserDao();
        for(long i=1;i<10;i++){
            User user = new User();
            user.setId(i);
            user.setUserName("张三"+i);
            user.setPassword("123456"+i);
            userDao.add(user);
        }

    }

    @Test
    public void update(){
        UserDao userDao = new UserDao();
        User user = new User();
        user.setId(3L);
        user.setUserName("李四");
        user.setPassword("789456123");
        userDao.update(user);
    }

    @Test
    public void delete(){
        UserDao userDao = new UserDao();
        userDao.delete(3l);
    }

    @Test
    public void getById(){
        UserDao userDao = new UserDao();
        User user = userDao.getById(1L);
        System.out.println(user);
    }

}
