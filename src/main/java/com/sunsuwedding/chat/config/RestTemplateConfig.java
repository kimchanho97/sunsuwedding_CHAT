package com.sunsuwedding.chat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);    // 2초 연결 타임아웃
        factory.setReadTimeout(5000);       // 5초 읽기 타임아웃
        return new RestTemplate(factory);
    }

    @Bean
    @Qualifier("realtimeRestTemplate")
    public RestTemplate realtimeRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);    // 1초 연결 타임아웃
        factory.setReadTimeout(2000);       // 2초 읽기 타임아웃
        return new RestTemplate(factory);
    }

}
