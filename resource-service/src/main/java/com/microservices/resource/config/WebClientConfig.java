package com.microservices.resource.config;

import com.microservices.resource.client.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value(value = "${url.gateway}")
    private String gatewayUrl;

    @Bean
    public StorageClient storageServiceClient(WebClient.Builder builder) {
        WebClient client = builder
                .baseUrl(gatewayUrl)
                .filter(retryFilter())
                .build();

        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client))
                        .blockTimeout(Duration.ofMinutes(1))
                        .build();

        return proxyFactory.createClient(StorageClient.class);
    }

    private ExchangeFilterFunction retryFilter() {
        return (request, next) ->
                next.exchange(request)
                        .retryWhen(
                                Retry.fixedDelay(3, Duration.ofSeconds(3))
                                        .doAfterRetry(retrySignal -> log.warn("Retrying {} request to {}", request.method().name(), request.url())));
    }
}

