package com.itajk.filter;


import javax.servlet.*;
import java.io.IOException;

/**
 * @author JK_a
 * @version V1.0
 * @Description:用来解决中文字符集乱码需要实现Filter接口，并重写doFilter函数
 * @className: EncodingFilter
 * @date 2021/11/5 10:44
 * @company:华勤技术股份有限公司
 * @copyright: Copyright (c) 2021
 */
public class EncodingFilter implements Filter {

    public EncodingFilter(){
        System.out.println("过滤器构造");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //将编码改为UTF-8
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        System.out.println("过滤器销毁");
    }
}
