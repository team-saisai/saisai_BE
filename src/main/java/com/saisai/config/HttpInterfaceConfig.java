package com.saisai.config;

import com.saisai.domain.course.api.DurunubiCourseApiClient;
import com.saisai.domain.checkpoint.client.DurunubiCheckpointClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public DurunubiCourseApiClient courseApiClient(RestClient restClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build();

        return factory.createClient(DurunubiCourseApiClient.class);
    }

    @Bean
    public DurunubiCheckpointClient checkpointApiClient(RestClient restClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build();

        return factory.createClient(DurunubiCheckpointClient.class);
    }

}
