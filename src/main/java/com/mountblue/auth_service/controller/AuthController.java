package com.mountblue.auth_service.controller;

import com.mountblue.auth_service.dto.LoginRequestDto;
import com.mountblue.auth_service.dto.LoginResponseDto;
import com.mountblue.auth_service.dto.RegisterUserDto;
import com.mountblue.auth_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterUserDto dto) {
        authService.registerUser(dto);
        return "Registration successful";
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "Logout successful";
    }
}