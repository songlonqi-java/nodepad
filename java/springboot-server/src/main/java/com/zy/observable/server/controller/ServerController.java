package com.zy.observable.server.controller;

import com.zy.observable.server.bean.AjaxResult;
import com.zy.observable.server.dao.BookDao;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * @author liurui
 * @date 2022/05/11 14:32
 */
@CrossOrigin
@Controller
public class ServerController {
    @Autowired
    public RestTemplate httpTemplate;

    @Autowired
    public BookDao bookDao;
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Value("${client:false}")
    private Boolean client;

    @Value("${api.url}")
    public String apiUrl;

    @Value("${extra.host}")
    public String clientHost;

    @Value("${sleep:0}")
    public Long sleep;

    @GetMapping("/")
    public String index() {
        return "index";
    }

//    @GetMapping("/gateway")
    @RequestMapping("/gateway")
    @ResponseBody
    public AjaxResult gateway(String tag) {
        logger.info("this is tag");
        sleep();
        httpTemplate.getForEntity(apiUrl + "/resource", String.class).getBody();
        httpTemplate.getForEntity(apiUrl + "/auth", String.class).getBody();
        if (client) {
            try {
                httpTemplate.getForEntity("http://" + clientHost + ":8081/client", String.class).getBody();
            }catch (Exception e){
                return AjaxResult.error("client 调用失败");
            }
        }
        return httpTemplate.getForEntity(apiUrl + "/billing?tag=" + tag, AjaxResult.class).getBody();
    }

    @RequestMapping("/resource")
    @ResponseBody
    public AjaxResult resource() {
        logger.info("this is resource");
        return AjaxResult.success("this is resource");
    }

    @RequestMapping("/jsonStr")
    public void addJsonStr(HttpServletRequest request,HttpServletResponse response) {
        String jsonStr = "{\"a\":\"b\"}";
        logger.info("add jsonStr:{}", jsonStr);
        response.setContentType("application/json");
        try {
          //  ServletOutputStream outputStream = response.getOutputStream();
           // outputStream.write(jsonStr.getBytes());
            PrintWriter writer = response.getWriter();
            writer.write(jsonStr);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @RequestMapping("/download")
    public Object download(HttpServletRequest request, HttpServletResponse response) {
        logger.info("download file");

        final String fileName = "dd-java-agent-1.30.4-guance.jar";
        if (!StringUtils.isEmpty(fileName)) {
            // 下载
            final File file = new File("/home/songlq/logs", fileName);
            try {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            response.setContentType("application/octet-stream");
            try (final ServletOutputStream outputStream = response.getOutputStream()){

                Files.copy(Paths.get(file.getPath()), outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "ok";
    }

    @RequestMapping("/auth")
    @ResponseBody
    public AjaxResult auth() {
        for (int i = 0; i < 100; i++) {
            logger.info("this is auth conditions:map[string]filter.WhereConditions, rawConditions:map[string]string,)"+i);
        }

        sleep();
        return AjaxResult.success("this is auth");
    }

    @RequestMapping("/rocketmq")
    @ResponseBody
    public AjaxResult rocketmq() throws Exception{
        // 创建生产者实例
        DefaultMQProducer producer = new DefaultMQProducer("producer_group");

        // 设置NameServer地址，多个地址用分号隔开
        producer.setNamesrvAddr("localhost:9876");

        // 启动生产者
        producer.start();
        try {
            // 创建消息实例，指定Topic、Tag和消息内容
            Message message = new Message("test_topic", "tag", "Hello, RocketMQ!".getBytes());

            // 发送消息并获取发送结果
            SendResult sendResult = producer.send(message);

            System.out.println("消息发送成功，消息ID：" + sendResult.getMsgId());
            System.out.println("--------------------------------------------" );
        } catch (Exception e){
            System.out.println(e);
        }finally {
            // 关闭生产者
            producer.shutdown();
        }

        sleep();
        return AjaxResult.success("send rocketmq message");
    }

    @RequestMapping("/billing")
    @ResponseBody
    public AjaxResult billing(String tag) {
        logger.info("this is method3,{}", tag);
        sleep();
        try {
            if (Optional.ofNullable(tag).get().equalsIgnoreCase("error")) {
                System.out.println(1 / 0);
            }
        }catch (Exception e){
            System.out.println(e);
        }

        return AjaxResult.success("下单成功");
    }

    private void sleep() {
        if (sleep>0L) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/getClient")
    @ResponseBody
    public String getClient() {
        return result();
    }

    @RequestMapping("/setClient")
    @ResponseBody
    public String setClient(Boolean c) {
        client = c;
        return result();
    }

    @RequestMapping("/sleep")
    @ResponseBody
    public String setSleep(Long sleep){
        this.sleep = sleep;
        return "休眠["+sleep+" ms ]时间设置成功";
    }

    @GetMapping("/testError")
    @ResponseBody
    public AjaxResult error(){
        return new AjaxResult(400,"异常测试");
    }
/*
    @Resource
    private MongoTemplate mongoTemplate;


    @RequestMapping("/mongo")
    @ResponseBody
    public String mongoDB() {
        // 插入一条数据
        Book book = new Book(2L,"zhongguo",8);
        bookDao.save(book);
        //bookDao.deleteById(2);
       // 查询一条数据
        Example<Book> example = Example.of(book);
        Optional<Book> one = bookDao.findOne(example);
        System.out.println(one);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(5));
        Book b5 = mongoTemplate.findOne(query,Book.class);
        System.out.println("----------------");
        System.out.println(b5);

        Book book2 = new Book(6L,"zhongguo",8);
        bookDao.save(book2);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            System.out.println(e);
        }

        bookDao.deleteById(2);
        //bookDao.deleteById(4);

        return "mongodb save and delete";
    }
*/

    private String result() {
        return client ? "【已开启】客户端请求" : "【已关闭】客户端请求";
    }
}
