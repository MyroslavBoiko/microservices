package com.microservices.resourceprocessor.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.List;


public class SongServiceConfiguration {


    @Bean
    ServiceInstanceListSupplier songSupplier(DiscoveryClient discoveryClient) {
        List<ServiceInstance> instances = discoveryClient.getInstances("song-service");
        return new DemoServiceInstanceListSuppler("song-service", instances);
    }

    static class DemoServiceInstanceListSuppler implements ServiceInstanceListSupplier {

        private final String serviceId;
        private final List<ServiceInstance> serviceInstances;

        DemoServiceInstanceListSuppler(String serviceId, List<ServiceInstance> serviceInstances) {
            this.serviceId = serviceId;
            this.serviceInstances = serviceInstances;
        }

        @Override
        public String getServiceId() {
            return serviceId;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
            return Flux.just(serviceInstances);

        }
    }

}
