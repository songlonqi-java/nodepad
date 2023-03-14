package com.itajk.util;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author JK_a
 * @version V1.0
 * @Description:数据库的连接与关闭
 * @className: DaBaConnect
 * @date 2021/11/5 10:46
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class DaBaConnect {
    static String url = "jdbc:mysql://49.232.153.84:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT";
    static String user = "root";
    static String pwd = "123456#..A";
    static Connection conn=null;
    static PreparedStatement ps=null;
    static ResultSet rs=null;
    static Statement st=null;

    /**
     * SQL程序初始化
     * @throws SQLException
     * @throws ClassNotFoundException
     */

    public static void init() throws SQLException,ClassNotFoundException {
        try{
            //注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver"); //com.mysql.jdbc.Driver
            //建立连接
            conn = DriverManager.getConnection(url, user, pwd);
        }catch (Exception e){
            System.out.println("SQL程序初始化失败");
            e.printStackTrace();
        }
    }
    public static java.sql.Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "SYSTEM", "123456");
    }
    /**
     * 数据库增删改
     * @param sql
     * @return
     */
    public static int AddUpdateDelete(String sql){
        int i = 0;
        try{
            ps = conn.prepareStatement(sql);
         //   ps.setString(0,"aaa");
            boolean flag = ps.execute();
            //如果第一个结果是结果集对象，则返回true，如果第一个结果是更新计数或者没有结果，则返回false
            if (flag == false){
                i++;
            }
        }catch (Exception e){
            System.out.println("数据库增删改异常");
            e.printStackTrace();
        }
        return i;
    }
    public static int AddUpdateDeleteByID(String sql,String id){
        int i = 0;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,id);
            //   ps.setString(0,"aaa");
            boolean flag = ps.execute();
            //如果第一个结果是结果集对象，则返回true，如果第一个结果是更新计数或者没有结果，则返回false
            if (flag == false){
                i++;
            }
        }catch (Exception e){
            System.out.println("数据库增删改异常");
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 数据库查询
     * @param sql
     * @return
     */
    public static ResultSet selectSql(String sql){
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        }catch (Exception e){
            System.out.println("数据库查询异常");
            e.printStackTrace();
        }
        return rs;
    }
    public static ResultSet selectSqlUser(String sql,String username,String pw){
        try {
            ps = conn.prepareStatement("SELECT name,password,id FROM student where name=? and password=?");
            ps.setString(1,username);
            ps.setString(2,pw);
            rs = ps.executeQuery();
        }catch (Exception e){
            System.out.println("数据库查询异常");
            e.printStackTrace();
        }
        return rs;
    }
    /**
     * 数据库关闭
     */
    public static void closeConn(){
        try{
            conn.close();
        }catch (Exception e){
            System.out.println("数据库关闭异常");
            e.printStackTrace();
        }
    }
}
