package com.refda.bookstore.controller;

import com.refda.bookstore.dto.JwtResponse;
import com.refda.bookstore.dto.LoginRequest;
import com.refda.bookstore.dto.MessageResponse;
import com.refda.bookstore.dto.RegisterRequest;
import com.refda.bookstore.model.Role;
import com.refda.bookstore.model.User;
import com.refda.bookstore.repository.UserRepository;
import com.refda.bookstore.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600) // Biar bisa diakses dari Frontend/Postman mana aja
@RestController
@RequestMapping("/auth") // Base URL: http://localhost:8080/auth
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // Endpoint: POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Cek email & password via AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Set Security Context (User sedang login)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate Token JWT
        String jwt = jwtUtils.generateToken(loginRequest.getEmail());

        // 4. Ambil data user untuk response
        org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        // 5. Return JSON
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), role));
    }

    // Endpoint: POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        // 1. Validasi Email Unik
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // 2. Create User Baru
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword())); // Hash Password

        // Default Role = USER kalau tidak diisi
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.USER);

        // 3. Simpan ke DB
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}