package com.nagarro.auth_service.service;

import com.nagarro.auth_service.dto.LoginRequestDto;
import com.nagarro.auth_service.dto.LoginResponseDto;
import com.nagarro.auth_service.dto.SignupRequestDto;
import com.nagarro.auth_service.dto.SignupResponseDto;
import com.nagarro.auth_service.exception.InvalidCredentialsException;
import com.nagarro.auth_service.exception.NotFoundException;
import com.nagarro.auth_service.exception.TooManyLoginAttemptsException;
import com.nagarro.auth_service.exception.UserAlreadyExistsException;
import com.nagarro.auth_service.model.Role;
import com.nagarro.auth_service.model.User;
import com.nagarro.auth_service.repository.RoleRepository;
import com.nagarro.auth_service.repository.UserRepository;
import com.nagarro.auth_service.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final RoleRepository roleRepository;
    private final LoginAttemptService loginAttemptService;

    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        User user = userRepository.findByUsername(signupRequestDto.getUsername());
        if(user!=null) throw new UserAlreadyExistsException("User already exists");
        if (signupRequestDto.getUsername()== null || signupRequestDto.getUsername().trim().isEmpty() || signupRequestDto.getPassword()== null || signupRequestDto.getPassword().trim().isEmpty()){
            throw new InvalidCredentialsException("Username or password cannot be empty");
        }

        Role userRole = roleRepository.findByRoleName("ROLE_PATIENT").orElseThrow(() -> new NotFoundException("Default user not found"));
        if (!signupRequestDto.getUsername().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidCredentialsException("Username must be a valid email address");
        }
        user = userRepository.save(User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .role(userRole)
                .build());
        String token = authUtil.generateAccessToken(user);
        System.out.println("JWT token: " + token);
        return new SignupResponseDto(user.getId(),user.getUsername());

    }

    public SignupResponseDto signupDoctor(SignupRequestDto signupRequestDto) {
        if (signupRequestDto.getUsername()== null || signupRequestDto.getUsername().trim().isEmpty() || signupRequestDto.getPassword()== null || signupRequestDto.getPassword().trim().isEmpty()){
            throw new InvalidCredentialsException("Username or password cannot be empty");
        }
        User user = userRepository.findByUsername(signupRequestDto.getUsername());
        if (user != null) throw new UserAlreadyExistsException("User already exists");

        if (signupRequestDto.getUsername() == null || signupRequestDto.getUsername().trim().isEmpty() ||
                signupRequestDto.getPassword() == null || signupRequestDto.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsException("Username or password cannot be empty");
        }

        Role doctorRole = roleRepository.findByRoleName("ROLE_DOCTOR")
                .orElseThrow(() -> new NotFoundException("Doctor role not found"));

        user = userRepository.save(User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .role(doctorRole)
                .build());

        // No token generation here
        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        if (loginRequestDto.getUsername()== null || loginRequestDto.getUsername().trim().isEmpty() || loginRequestDto.getPassword()== null || loginRequestDto.getPassword().trim().isEmpty()){
            throw new InvalidCredentialsException("Username or password cannot be empty");
        }
        if (loginAttemptService.isBlocked(loginRequestDto.getUsername())) {
            throw new TooManyLoginAttemptsException("Too many failed attempts. Try again later.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );
            loginAttemptService.loginSucceeded(loginRequestDto.getUsername());
        } catch(InvalidCredentialsException ex) {
            loginAttemptService.loginFailed(loginRequestDto.getUsername());
            throw ex;
        }
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token, user.getId());
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
