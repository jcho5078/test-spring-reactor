package com.example.reactorspring.web;

import com.example.reactorspring.jwt.LoginRequest;
import com.example.reactorspring.jwt.LoginResponse;
import com.example.reactorspring.jwt.ProfileResponse;
import com.example.reactorspring.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final ReactiveUserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    Mono<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return userDetailsService.findByUsername(loginRequest.username())
                .filter(u -> passwordEncoder.matches(loginRequest.password(), u.getPassword()))
                .map(tokenProvider::getToken)
                .map(LoginResponse::new)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
    }

    @PostMapping("/login/oauth2/naver")
    public Mono<LoginResponse> oauthLogin(@RequestBody LoginRequest loginRequest){
        return userDetailsService.findByUsername(loginRequest.username())
                .filter(u -> passwordEncoder.matches(loginRequest.password(), u.getPassword()))
                .map(tokenProvider::getToken)
                .map(LoginResponse::new)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
    }

    @GetMapping("/login/oauth2/code/status")
    public Boolean redirectOauthLogin(){
        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/profile")
    Mono<ProfileResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return Mono.just(new ProfileResponse(user.getUsername(), user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(name -> name.substring("ROLE_".length()))
                .collect(Collectors.toSet())
        ));
    }
}