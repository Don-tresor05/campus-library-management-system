package clms.system.clms_backend.service;

import clms.system.clms_backend.dto.LoginRequest;
import clms.system.clms_backend.dto.RegisterRequest;
import clms.system.clms_backend.model.User;
import clms.system.clms_backend.repository.UserRepository;
import clms.system.clms_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getApproved())) {
            throw new RuntimeException("Your account is pending admin approval.");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Your account is deactivated. Please contact the administrator.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole().name());
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());

        return response;
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        // Self-registered users start as STUDENT until approved.
        user.setRole(User.UserRole.STUDENT);
        user.setRequestedRole(request.getRole() != null ? request.getRole() : User.UserRole.STUDENT);
        user.setActive(true);
        user.setApproved(false);

        User savedUser = userRepository.save(user);

        // Do not auto-login unapproved accounts; just inform the client.
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. Your account is pending admin approval.");
        response.put("username", savedUser.getUsername());
        response.put("requestedRole", savedUser.getRequestedRole().name());
        return response;
    }
}





