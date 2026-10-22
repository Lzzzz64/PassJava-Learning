
package org.example.passjava.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            @Value("${app.jwt.secret}") String secret) {

        byte[] bytes = Base64.getDecoder().decode(secret);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT密钥至少需要32字节");
        }

        SecretKey key =
                new SecretKeySpec(bytes, "HmacSHA256");

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtConverter() {

        JwtGrantedAuthoritiesConverter authorities =
                new JwtGrantedAuthoritiesConverter();

        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(authorities);

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverterAdapter converter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth

                        // 注册、登录允许匿名访问
                        .pathMatchers(HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login")
                        .permitAll()

                        // 公开题库查询
                        .pathMatchers(HttpMethod.GET,
                                "/api/questions",
                                "/api/questions/**")
                        .permitAll()

                        // 管理员新增、修改、删除题目
                        .pathMatchers(HttpMethod.POST,
                                "/api/questions",
                                "/api/questions/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT,
                                "/api/questions/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE,
                                "/api/questions/**")
                        .hasRole("ADMIN")

                        // Feign 演示接口只允许管理员访问
                        .pathMatchers(HttpMethod.GET,
                                "/api/admin/questions/**")
                        .hasRole("ADMIN")

                        // 个人信息等剩余合法接口需要登录
                        .pathMatchers("/api/auth/me")
                        .authenticated()

                        // 未明确开放的地址全部拒绝
                        .anyExchange()
                        .denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(converter)))
                .build();
    }
}
