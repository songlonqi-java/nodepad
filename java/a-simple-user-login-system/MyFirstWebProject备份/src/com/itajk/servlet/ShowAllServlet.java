package com.itajk.servlet;

import com.itajk.dao.UserDao;
import com.itajk.dao.UserDaoImpl;
import com.itajk.entity.MyUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JK_a
 * @version V1.0
 * @Description:返回数据库中所有用户信息
 * @className: ShowAllServlet
 * @date 2021/11/5 10:45
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class ShowAllServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html,charset=utf-8");
        UserDao ud = new UserDaoImpl();
        List<MyUser> userAll = ud.getUserAll();
        req.setAttribute("all",userAll);
        req.getRequestDispatcher("ShowAll.jsp").forward(req,resp);
    }
}
