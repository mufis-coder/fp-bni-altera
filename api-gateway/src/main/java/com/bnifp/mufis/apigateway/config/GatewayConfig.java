package com.bnifp.mufis.apigateway.config;
import com.bnifp.mufis.apigateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("post", r -> r.path("/posts/**")
                        .filters(f -> f.filter(filter)).uri("lb://post-service"))
                .route("post-like", r -> r.path("/post-likes/**")
                        .filters(f -> f.filter(filter)).uri("lb://post-service"))
                .route("post-comment", r -> r.path("/post-comments/**")
                        .filters(f -> f.filter(filter)).uri("lb://post-service"))
                .route("user", r->r.path("/users/**")
                        .filters(f -> f.filter(filter)).uri("lb://auth-service"))
                .route("log", r->r.path("/logs/**").
                        filters(f -> f.filter(filter)).uri("lb://log-service"))
                .route("category", r->r.path("/categories/**").
                        filters(f -> f.filter(filter)).uri("lb://category-service"))
                .route("user-category", r->r.path("/user-categories/**").
                        filters(f -> f.filter(filter)).uri("lb://category-service"))
                .route("post-category", r->r.path("/post-categories/**").
                        filters(f -> f.filter(filter)).uri("lb://category-service"))
                .route("auth", r->r.path("/auths/**").uri("lb://auth-service"))
                .route("post-swagger", r->r.path("/post-swagger/**").uri("lb://post-service"))
                .route("auth-swagger", r->r.path("/auth-swagger/**").uri("lb://auth-service"))
                .route("log-swagger", r->r.path("/log-swagger/**").uri("lb://log-service"))
                .route("category-swagger", r->r.path("/category-swagger/**").uri("lb://category-service"))
                .build();
    }

}

