package com.example.reactorspring.jwt;

import java.util.Set;

public record ProfileResponse(String username, Set<String> roles) {
}
