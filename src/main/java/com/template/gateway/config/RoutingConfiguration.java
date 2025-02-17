package com.template.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoutingConfiguration {
    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("TEMPLATE-AUTH", r -> r.path("/template-auth/api/**").uri("lb://TEMPLATE-AUTH"))
                .route("TEMPLATE-CORE", r -> r.path("/template-core/api/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://TEMPLATE-CORE")
                )
                .build();
    }

}