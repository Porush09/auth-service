package com.mountblue.auth_service.controller;

import com.mountblue.auth_service.dto.UserInfoDto;
import com.mountblue.auth_service.entity.User;
import com.mountblue.auth_service.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserRepository userRepository;

    public UserApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public UserInfoDto getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setChannelName(user.getChannelName());

        return dto;
    }
}
