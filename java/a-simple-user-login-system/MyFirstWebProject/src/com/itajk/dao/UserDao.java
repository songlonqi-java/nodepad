package com.itajk.dao;

import com.itajk.entity.MyUser;

import java.util.List;

/**
 * @author JK_a
 * @version V1.0
 * @Description:
 * @className: UserDao
 * @date 2021/11/5 10:42
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public interface UserDao {
    /**
     * 用户是否登录
     * @param name
     * @param password
     * @return
     */
    public boolean login(String name,String password);

    /**
     * 用户是否注册
     * @param user
     * @return
     */
    public boolean register(MyUser user);

    /**
     * 返回用户信息集合
     * @return
     */
    public List<MyUser> getUserAll();

    /**
     * 根据id删除
     * @param id
     * @return
     */
    public boolean delete(String id);

    public boolean update(String name,String password,String id);
}
