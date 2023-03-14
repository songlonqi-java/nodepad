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
 * @Description:更新用户信息（目前仅仅是根据用户id来进行更新）
 * @className: UpdateServlet
 * @date 2021/11/5 10:45
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class UpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /**
         * 获取jsp页面传过来的参数
         */
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String password = req.getParameter("password");

        UserDao ud = new UserDaoImpl();
        if (ud.update(name,password,id)){
            req.getRequestDispatcher("AddUpdateDeleteSuccess.jsp").forward(req,resp);
        }else {
            resp.sendRedirect("AddUpdateDeleteFail.jsp");
        }
    }
}
