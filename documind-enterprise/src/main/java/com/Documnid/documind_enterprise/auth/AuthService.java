package com.Documnid.documind_enterprise.auth;

import com.Documnid.documind_enterprise.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        Department department = null;
        if (request.getDepartmentName() != null && !request.getDepartmentName().isBlank()) {
            department = departmentRepository.findByName(request.getDepartmentName())
                    .orElseGet(() -> departmentRepository.save(
                            Department.builder()
                                    .name(request.getDepartmentName())
                                    .description("Auto-created department")
                                    .build()
                    ));
        }

        RoleType assignedRole = request.getRole() != null ? request.getRole() : RoleType.ROLE_UPLOADER;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .department(department)
                .active(true)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .department(department != null ? department.getName() : "N/A")
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .department(user.getDepartment() != null ? user.getDepartment().getName() : "N/A")
                .message("Login successful")
                .build();
    }
}