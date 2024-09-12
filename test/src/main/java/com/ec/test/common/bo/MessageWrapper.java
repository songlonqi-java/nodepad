package com.ec.test.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * @Description: 消息包装器
 * @author: lChen
 * @date: 2023-04-24 16:10
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private T message;

    /**
     * 唯一标识，用于客户端幂等验证
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    private Long timestamp = System.currentTimeMillis();


    public MessageWrapper(T message) {
        this.message = message;
    }

}
