package com.itajk.dao;

import com.itajk.entity.MyUser;
import com.itajk.util.DaBaConnect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JK_a
 * @version V1.0
 * @Description:
 * @className: UserDaoImpl
 * @date 2021/11/5 10:42
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class UserDaoImpl implements UserDao{
    @Override
    public boolean login(String name, String password) {
        boolean flag = false;
        try{
            try{
                DaBaConnect.init();
            }catch (Exception e){
                e.printStackTrace();
            }
            //注意查询语句中的单引号和双引号
            ResultSet rs = DaBaConnect.selectSql("select * from student where name = '" + name + "'and password = '" + password + "';");
            while (rs.next()){
                if (rs.getString("name").equals(name)&&rs.getString("password").equals(password)){
                    flag = true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean register(MyUser user) {
        boolean flag = false;

            try{
                DaBaConnect.init();
            }catch (Exception e){
                e.printStackTrace();
            }
            int i = DaBaConnect.AddUpdateDelete("insert into student(name,password,id)"+
                    "values('"+user.getName()+"','"+user.getPassword()+"','"+user.getId()+"')");
            if (i > 0){
                flag = true;
            }
            DaBaConnect.closeConn();
            return flag;
    }

    /**
     * 返回用户信息集合
     * @return
     */
    @Override
    public List<MyUser> getUserAll() {
        List<MyUser> list = new ArrayList<>();
        try{
            try{
                DaBaConnect.init();
            }catch (Exception e){
                e.printStackTrace();
            }
            ResultSet rs = DaBaConnect.selectSql("select * from student");
            while (rs.next()){
                String name2 = rs.getString("name");
                String password2 = rs.getString("password");
                String id2 = rs.getString("id");
                MyUser user  = new MyUser(name2,password2,id2);
                list.add(user);
            }
            DaBaConnect.closeConn();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据id进行删除
     * @param id
     * @return
     */
    @Override
    public boolean delete(String id) {
        boolean flag = false;
        try{
            DaBaConnect.init();
        }catch (Exception e){
            e.printStackTrace();
        }
        String sql = "delete from student where id = '"+id+"'";
        /**
         * i的意义
         */
        int i = DaBaConnect.AddUpdateDelete(sql);
        if (i > 0){
            flag = true;
        }
        DaBaConnect.closeConn();
        return flag;
    }

    /**
     *
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean update(String name, String password,String id) {
        boolean flag = false;
        try{
            DaBaConnect.init();
        }catch (Exception e){
            e.printStackTrace();
        }
        String sql = "update student set name = '"+name+"',password = '"+password+"'"+"where id = '"+id+"'";
        System.out.println("update sql = "+sql);
        int i = DaBaConnect.AddUpdateDelete(sql);
        System.out.println("1"+""+i );
        if (i > 0){
            flag = true;
        }
        DaBaConnect.closeConn();
        return flag;
    }
}
