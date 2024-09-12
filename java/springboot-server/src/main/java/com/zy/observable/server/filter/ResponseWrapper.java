package com.zy.observable.server.filter;


import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


public class ResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter charArrayWriter;
    private Boolean useOutput;
    private Boolean useWrite;
    private Boolean isJson;
    private PrintWriter writer;

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private PrintStream printStream = new PrintStream(byteArrayOutputStream);
    private ServletOutputStream servletOutputStream = new CustomServletOutputStream(printStream);

    public ResponseWrapper(HttpServletResponse httpServletResponse)
    {
        super(httpServletResponse);
         charArrayWriter = new CharArrayWriter();
        writer = new PrintWriter(charArrayWriter);
        useWrite = false;
        useOutput = false;
        isJson = false;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        useWrite = true;
        System.out.println("get write");
        return writer;
    }


    public void flushBuffer() throws IOException {
        if (getIsJson()) {
            // Print the JSON response body
            System.out.println("JSON Response Body: " + charArrayWriter.toString());
        }
        PrintWriter responseWriter = super.getWriter();
        responseWriter.write(charArrayWriter.toString());
        responseWriter.flush();
    }

    @Override
    public void setContentType(String type) {
        System.out.println("set type = "+type);
        if (type.contains("application/json")){
            System.out.println("set json");
            isJson = true;
        }
        super.setContentType(type);
    }

    @Override
    public String getContentType() {
        System.out.println("getContentType");
        return super.getContentType();
    }

    @Override
    public void setHeader(String name, String value) {
        System.out.println("header: "+name+" value: "+value);
        super.setHeader(name, value);
    }

    @Override
    public void finalize() {
        try {
            flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getIsJson(){
        if (this.getContentType().contains("application/json")){
            isJson = true;
        }

        return isJson;
    }

    public Boolean getUseWrite() {
        return useWrite;
    }

    public Boolean getUseOutput() {
        return useOutput;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        System.out.println("getOutputStream");
        System.out.println("--- content type:"+getContentType());
        if (getIsJson()){
            useOutput = true;
            return servletOutputStream;
        }
        return super.getOutputStream();
    }

    public void flushStreamBuffer() throws IOException {
        if (getIsJson()) {
            byte[] bts =  byteArrayOutputStream.toByteArray();
            System.out.println("----- output and bts len = "+bts.length);
            // 写到客户端
            // Finally, copy the captured data to the actual response

            ServletOutputStream out = getResponse().getOutputStream();
            out.write(bts);
            out.flush();
        }
    }

    private static class CustomServletOutputStream extends ServletOutputStream {

        private PrintStream printStream;

        public CustomServletOutputStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // No implementation needed
        }

        @Override
        public void write(int b) throws IOException {
            printStream.write(b);
        }
    }
}