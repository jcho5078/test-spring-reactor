package com.example.reactorspring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateRequest {
    private String name;
    private String email;
}