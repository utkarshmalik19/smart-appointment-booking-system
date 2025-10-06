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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private AuthUtil authUtil;
    @Mock private RoleRepository roleRepository;
    @Mock private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- SIGNUP TESTS ---

    @Test
    void signup_ShouldThrow_WhenUserAlreadyExists() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("test@example.com");
        req.setPassword("pass");
        when(userRepository.findByUsername("test@example.com")).thenReturn(new User());

        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(req));
    }

    @Test
    void signup_ShouldThrow_WhenInvalidUsernameOrPassword() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("   "); // blank username
        req.setPassword(" ");

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.signup(req));
    }

    @Test
    void signup_ShouldThrow_WhenInvalidEmailFormat() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("notanemail");
        req.setPassword("pass");
        when(userRepository.findByUsername("notanemail")).thenReturn(null);
        when(roleRepository.findByRoleName("ROLE_PATIENT")).thenReturn(Optional.of(new Role()));

        assertThrows(InvalidCredentialsException.class, () -> authService.signup(req));
    }

    @Test
    void signup_ShouldReturnResponse_WhenValidData() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("valid@example.com");
        req.setPassword("pass");

        Role role = new Role();
        role.setRoleName("ROLE_PATIENT");

        User savedUser = User.builder()
                .id(1L)
                .username("valid@example.com")
                .password("encoded")
                .role(role)
                .build();

        when(userRepository.findByUsername("valid@example.com")).thenReturn(null);
        when(roleRepository.findByRoleName("ROLE_PATIENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authUtil.generateAccessToken(savedUser)).thenReturn("jwt-token");

        SignupResponseDto res = authService.signup(req);

        assertEquals("valid@example.com", res.getUsername());
        assertEquals(1L, res.getId());
        verify(userRepository).save(any(User.class));
    }

    // --- SIGNUP DOCTOR TESTS ---

    @Test
    void signupDoctor_ShouldThrow_WhenUserExists() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("doc@example.com");
        req.setPassword("pass");
        when(userRepository.findByUsername("doc@example.com")).thenReturn(new User());

        assertThrows(UserAlreadyExistsException.class, () -> authService.signupDoctor(req));
    }

    @Test
    void signupDoctor_ShouldReturnResponse_WhenValid() {
        SignupRequestDto req = new SignupRequestDto();
        req.setUsername("doc@example.com");
        req.setPassword("pass");

        Role doctorRole = new Role();
        doctorRole.setRoleName("ROLE_DOCTOR");

        User savedUser = User.builder()
                .id(10L)
                .username("doc@example.com")
                .password("encoded")
                .role(doctorRole)
                .build();

        when(userRepository.findByUsername("doc@example.com")).thenReturn(null);
        when(roleRepository.findByRoleName("ROLE_DOCTOR")).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        SignupResponseDto res = authService.signupDoctor(req);

        assertEquals(10L, res.getId());
        assertEquals("doc@example.com", res.getUsername());
    }

    // --- LOGIN TESTS ---

    @Test
    void login_ShouldThrow_WhenInvalidInput() {
        LoginRequestDto req = new LoginRequestDto();
        req.setUsername(" ");
        req.setPassword(" ");
        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_ShouldThrow_WhenUserBlocked() {
        LoginRequestDto req = new LoginRequestDto();
        req.setUsername("blocked@example.com");
        req.setPassword("pass");
        when(loginAttemptService.isBlocked("blocked@example.com")).thenReturn(true);

        assertThrows(TooManyLoginAttemptsException.class, () -> authService.login(req));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        LoginRequestDto req = new LoginRequestDto();
        req.setUsername("user@example.com");
        req.setPassword("pass");

        User user = User.builder().id(1L).username("user@example.com").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        when(loginAttemptService.isBlocked("user@example.com")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(authUtil.generateAccessToken(user)).thenReturn("jwt-token");

        LoginResponseDto result = authService.login(req);

        assertEquals("jwt-token", result.getJwt());
        assertEquals(1L, result.getId());
        verify(loginAttemptService).loginSucceeded("user@example.com");
    }

    // --- GET USER TESTS ---

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> authService.getUserById(1L));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        User user = User.builder().id(1L).username("abc@example.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = authService.getUserById(1L);

        assertEquals("abc@example.com", found.getUsername());
    }
}