package com.zy.observable.server.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.observable.server.bean.AjaxResult;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


@Component
public class ResponseFilter implements Filter {
  private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    System.out.println("处理请求前： " + response.getContentType());
    ResponseWrapper wrapper = new ResponseWrapper(httpServletResponse);

    filterChain.doFilter(request, wrapper);

    System.out.println("处理 filter 之后：>>" + response.getContentType());
    System.out.println("wrapper filter 之后：>>" + wrapper.getContentType());
    /*wrapper.flushBuffer();

    String parse = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
    // 必须设置ContentLength
    response.setContentLength(JSON.toJSONBytes(parse).length);
    // 根据http accept来设置，我这里为了简便直接写json了
    response.setContentType("application/json");
    response.getOutputStream().write(JSON.toJSONBytes(parse));*/

    if (wrapper.getUseOutput()){
      System.out.println("ResponseFilter use output");
      wrapper.flushStreamBuffer();
      return;
    }

      if(wrapper.getUseWrite()){
        System.out.println("ResponseFilter use getWrite");
        wrapper.flushBuffer();
        return;
      }
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}

  @Override
  public void destroy() {}
}



