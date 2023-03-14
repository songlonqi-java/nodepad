package com.dushibao.dao;

import com.dushibao.model.User;
import com.dushibao.utils.DBUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    /**
     * 新增用户
     *
     * @param user
     */
    public void add(User user) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            conn = DBUtils.getConnection();
            stmt = conn.prepareStatement("insert into t_user values(?,sysdate,?,?,sysdate);");
            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getPassword());

            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(null, stmt, conn);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    public void update(User user) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            conn = DBUtils.getConnection();
            stmt = conn.prepareStatement("update t_user set userName=?,password=?,logTime=sysdate where id=?;");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(null, stmt, conn);
        }
    }

    /**
     * 删除用户
     * @param id
     */
    public void delete(Long id) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            conn = DBUtils.getConnection();
            stmt = conn.prepareStatement("delete from t_user where id=?;");
            stmt.setLong(1, id);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeAll(null, stmt, conn);
        }
    }

    /**
     * 根据ID查询
     *
     * @param id
     */
    public User getById(Long id){
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT id,addTime,userName,password,logTime FROM t_user WHERE id=?;";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1,id);
            rs = stmt.executeQuery();
            User user = new User();
            while(rs.next()){

                user.setId(rs.getLong(1));
                user.setAddTime(rs.getDate(2));
                user.setUserName(rs.getString(3));
                user.setPassword(rs.getString(4));
                user.setLogTime(rs.getDate(5));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            DBUtils.closeAll(null, stmt, conn);
        }
    }

}
