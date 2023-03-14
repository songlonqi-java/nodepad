package com.itajk.servlet;

import com.itajk.dao.UserDao;
import com.itajk.dao.UserDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author JK_a
 * @version V1.0
 * @Description:需要继承HttpServlrt，重写doGet,doPost方法
 * @className: LoginServlet
 * @date 2021/11/5 10:45
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 将信息使用doPost方法执行，对应jsp页面中form表的method
         */
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 得到jsp页面传过来的参数
         */
        String name = req.getParameter("name");
        String password = req.getParameter("password");

        UserDao ud = new UserDaoImpl();

        if (ud.login(name,password)){
            /**
             * 转发到成功页面
             */
            req.getRequestDispatcher("Success.jsp").forward(req,resp);
        }else {
            /**
             * 重定向到首页
             */
            resp.sendRedirect("LoginFail.jsp");
        }
    }
}
