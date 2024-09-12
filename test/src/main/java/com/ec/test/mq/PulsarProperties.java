package com.ec.test.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */

@Component
@ConfigurationProperties(prefix = "mq.pulsar")
@Data
public class PulsarProperties {

    /**
     * 接入地址
     */
    private String serviceUrl = "pulsar://10.200.14.188:6650";

    /**
     * 角色的token
     */
    //private String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LWNvbnN1bWVyIn0.rYTReKvSqDiWQDkGn8B9MUmVGqelypM4JcK9tX5Yl20gMS_MqZL_xRNuJpdYcJHPIx9eyaBcUkXb2w94ANwtr-c3mMGBCNKNGUH1pZy4Qm7TjKcX_TDAsHHa3ZUs_h5bnSFp5TK_Hv6_K3nnB4ydbhqgmLfephlGZ3SoknCy49-ifmmNTPjTqSg2ZSwIqomOkhhgQFq7Wyt-MgG9pyUJR-fncsNkx-r3RWT3dM7laObwElcldlTAQ-Eei12AhqN2CjTXcAdp5LS_sh2LF1a4nULjKivH6Ay3nJeBcnmiB29uEyaUoX97zCRF-8aWCrf3ViwdeB4XoZcQttQnFqet0Q";



}