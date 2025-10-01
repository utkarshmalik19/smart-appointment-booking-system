package com.nagarro.auth_service.controller;

import com.nagarro.auth_service.dto.LoginRequestDto;
import com.nagarro.auth_service.dto.LoginResponseDto;
import com.nagarro.auth_service.dto.SignupRequestDto;
import com.nagarro.auth_service.dto.SignupResponseDto;
import com.nagarro.auth_service.exception.NotFoundException;
import com.nagarro.auth_service.model.User;
import com.nagarro.auth_service.repository.UserRepository;
import com.nagarro.auth_service.security.AuthUtil;
import com.nagarro.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }
    // Admin creates doctor
    @PostMapping("/signup/doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignupResponseDto> signupDoctor(@RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.ok(authService.signupDoctor(signupRequestDto));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(authService.getUserById(id));
    }
    @GetMapping("/authenticate")
    public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = authUtil.getUserIdFromToken(token); // decode JWT
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new User(user.getId(), user.getUsername(), user.getPassword(),user.getRole());
    }

}
