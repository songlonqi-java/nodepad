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
 * @Description:删除用户信息
 * @className: DeleteServlet
 * @date 2021/11/5 10:44
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class DeleteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        UserDao ud = new UserDaoImpl();

        if(ud.delete(id)){
            req.getRequestDispatcher("AddUpdateDeleteSuccess.jsp").forward(req,resp);
        }else {
            resp.sendRedirect("AddUpdateDeleteFail.jsp");
        }
    }
}
