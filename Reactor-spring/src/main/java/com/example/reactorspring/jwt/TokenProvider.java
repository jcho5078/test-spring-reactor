package com.example.reactorspring.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenProvider {
    String getToken(UserDetails userDetails);
}
