package com.mountblue.auth_service.service;

import com.mountblue.auth_service.dto.LoginRequestDto;
import com.mountblue.auth_service.dto.LoginResponseDto;
import com.mountblue.auth_service.dto.RegisterUserDto;
import com.mountblue.auth_service.entity.User;
import com.mountblue.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerUser(RegisterUserDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByChannelName(dto.getChannelName())) {
            throw new RuntimeException("Channel name already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setChannelName(dto.getChannelName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole()
        );

        return new LoginResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getChannelName()
        );
    }
}

