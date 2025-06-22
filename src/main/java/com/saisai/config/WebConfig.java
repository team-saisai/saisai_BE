package com.saisai.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

@Configuration
public class WebConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // YYYY-MM-DDTHH:mm:ss 으로 표현

        return objectMapper;
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(EncodingMode.NONE);

        return builder
            .uriBuilderFactory(factory)
            .requestFactory(customHttpRequestFactory())
            .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {

        // 풀링 설정 (대량 요청 대비)
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // 최대 연결 수
        connectionManager.setDefaultMaxPerRoute(20); // 호스트 당 연결 수

        // 타임아웃 설정
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(5)) // 연결
            .setResponseTimeout(Timeout.ofSeconds(10)) // 데이터 읽기
            .build();

        // HttpClient 생성 및 설정
        HttpClient httpClient = HttpClientBuilder.create()
            .setConnectionManager(connectionManager) // 커넥션 매니저 적용
            .setDefaultRequestConfig(requestConfig) // 요청 설정 적용
            .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
