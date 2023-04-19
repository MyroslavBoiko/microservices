package com.microservices.resourceprocessor.config;

import com.microservices.resourceprocessor.client.ResourceServiceClient;
import com.microservices.resourceprocessor.client.SongServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
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

    @Value(value = "${url.song-service}")
    private String songServiceUrl;
    @Value(value = "${url.resource-service}")
    private String resourceServiceUrl;

    @Bean
    public SongServiceClient songServiceClient() {
        WebClient client = WebClient.builder()
                .baseUrl(songServiceUrl)
                .filter(retryFilter())
                .build();

        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client))
                        .blockTimeout(Duration.ofMinutes(1))
                        .build();

        return proxyFactory.createClient(SongServiceClient.class);
    }

    @Bean
    public ResourceServiceClient resourceServiceClient() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        WebClient client = WebClient.builder()
                .baseUrl(resourceServiceUrl)
                .exchangeStrategies(strategies)
                .filter(retryFilter())
                .build();

        HttpServiceProxyFactory proxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

        return proxyFactory.createClient(ResourceServiceClient.class);
    }

    private ExchangeFilterFunction retryFilter() {
        return (request, next) ->
                next.exchange(request)
                        .retryWhen(
                                Retry.fixedDelay(3, Duration.ofSeconds(3))
                                        .doAfterRetry(retrySignal -> log.warn("Retrying {} request to {}", request.method().name(), request.url())));
    }
}
