package com.mountblue.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String channelName;
    private String password;
}
