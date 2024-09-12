package com.ec.test.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 一次性mq vo类
 * @author: lChen
 * @date: 2023-09-15 12:10
 **/
@Data
@Accessors(chain = true)
public class OnceMqVo {

    /**
     * 队列名称
     */
    @NotBlank(message = "topicName can't be blank")
    private String topicName;

    /**
     * 消息内容
     */
    @NotBlank(message = "msgBody can't be blank")
    private String msgBody;

    /**
     * 消息键
     */
    @NotBlank(message = "msgKey can't be blank")
    private String msgKey;
}
