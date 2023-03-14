<%--
  Created by IntelliJ IDEA.
  User: X00004956
  Date: 2021/11/5
  Time: 10:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录注册页面</title>
</head>
<body>
<form action="LoginServlet" method="post" style="padding-top: -700px">
    用户名<input type="text" name="name" value=""><br><br>
    密码<input type="password" name="password" value=""><br><br>
    <input type="submit" value="登录" name="login"><input type="submit" value="重置"><br>
</form>

<form action="Register.jsp">
    <input type="submit" value="新用户注册">
</form>

</body>
</html>
