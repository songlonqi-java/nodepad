package com.itajk.servlet;

import com.itajk.dao.UserDao;
import com.itajk.dao.UserDaoImpl;
import com.itajk.entity.MyUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author JK_a
 * @version V1.0
 * @Description:实现用户注册的操作
 * @className: RegisterServlet
 * @date 2021/11/5 10:45
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 获取jsp页面传过来的参数
         */
        String name = req.getParameter("name");
        String pwd = req.getParameter("password");
        String id = req.getParameter("id");

        /**
         * 实例化一个对象，组装属性
         */
        MyUser user = new MyUser();
        user.setName(name);
        user.setPassword(pwd);
        user.setId(id);

        UserDao ud = new UserDaoImpl();
        if (ud.register(user)){
            /**
             * 向request域中放置参数
             */
            req.setAttribute("name",name);
            req.getRequestDispatcher("DoSuccess.jsp").forward(req,resp);
        }else {
            /**
             * 注册失败则返回注册失败页面
             */
            resp.sendRedirect("RegisterFail.jsp");
        }
    }
}
