<%--
  Created by IntelliJ IDEA.
  User: X00004956
  Date: 2021/11/5
  Time: 10:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.itajk.entity.MyUser" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.itajk.dao.UserDao" %>
<%@ page import="com.itajk.dao.UserDaoImpl" %>
<%@ page import="java.lang.ref.ReferenceQueue" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>所有用户页面</title>
</head>
<body>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
%>

<%--使用form提交数据，不能在页面没有刷新的情况下直接在当前页面显示后台数据--%>
<h2 style="text-align: center">用户信息表</h2>
<c:forEach var="U" items="${requestScope.all}">
    <form action="UpdateServlet" method="post" style="text-align: center">
        <tr>
            <td><input type="text" value="${U.name}" name="name"></td>
            <td><input type="text" value="${U.password}" name="password"></td>
            <td><input type="text" value="${U.id}" name="id"></td>
            <td><a href="DeleteServlet?id=${U.id}">删除</a><input type="submit" value="更新"/></td>
        </tr>
    </form>
</c:forEach>
<br>
<br>
<a href="Login.jsp">返回登录界面</a>
</body>
</html>
