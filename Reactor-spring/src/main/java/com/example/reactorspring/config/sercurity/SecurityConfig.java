package com.example.reactorspring.config.sercurity;

import com.example.reactorspring.security.OAuth2LoginSuccessHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authenticationManager,
                                                         ServerAuthenticationConverter authenticationConverter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Session Stateless
                .exceptionHandling()
                    .authenticationEntryPoint(serverAuthenticationEntryPoint())
                .and()
                .authorizeExchange()
                .pathMatchers("/login").permitAll()
                .anyExchange().authenticated()
                .and()
                .anonymous().disable()
                .oauth2Login()
                .authenticationSuccessHandler(new OAuth2LoginSuccessHandler()) // OAuth2 로그인 성공 시 커스텀 핸들러를 사용
                .and()
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer().jwt();

        return http.build();
    }

    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint(){
        return (exchange, authEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.error("Unauthorized error: {}", authEx.getMessage());
            log.error("Requested path    : {}", requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);

            Map<String, String> errorMessage = Map.of("state", Integer.toString(HttpStatus.UNAUTHORIZED.value())
                , "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                , "requestPath", requestPath
                , "message", authEx.getMessage());

            errorMessage.put("state", Integer.toString(HttpStatus.UNAUTHORIZED.value()));


            try {
                byte[] errorByte = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .writeValueAsBytes(errorMessage);
                DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
                return serverHttpResponse.writeWith(Mono.just(dataBuffer));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                return serverHttpResponse.setComplete();
            }
        };
    }
}
