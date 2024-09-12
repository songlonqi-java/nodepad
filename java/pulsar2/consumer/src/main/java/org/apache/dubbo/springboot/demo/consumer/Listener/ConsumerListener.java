package org.apache.dubbo.springboot.demo.consumer.Listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

@Component
@Slf4j
public class ConsumerListener extends AbstractMessageListener<String, byte[]>{

    public final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    @Override
    protected void onConsumer(String msg) {
        System.out.println("-------------"+msg);
       /* try {
          //  String body = restTemplate.getForEntity("http://localhost:8081/client", String.class).getBody();
            HttpUriRequest request = new HttpGet("http://localhost:8081/client");
            CloseableHttpResponse response = httpClient.execute(request);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = responseHandler.handleResponse(response);
            System.out.println("responseBody = " + responseBody);

            System.out.println("body");
            log.info("发送请求到client");
        }catch (Exception e){
            System.out.println(e);
        }*/
    }
}
