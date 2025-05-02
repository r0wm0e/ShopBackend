package org.example.shopbackend.auth;

import lombok.Data;

@Data
public class AuthRequest {

    String username;
    String password;
}
