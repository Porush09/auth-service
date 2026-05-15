package com.mountblue.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Long userId;
    private String email;
    private String channelName;
}