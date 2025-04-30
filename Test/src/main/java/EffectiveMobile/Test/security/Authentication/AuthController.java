package EffectiveMobile.Test.security.Authentication;


import EffectiveMobile.Test.DTO.*;
import EffectiveMobile.Test.entities.User;
import EffectiveMobile.Test.repositories.UserRepository;
import EffectiveMobile.Test.security.Authentication.Jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User already exists");
        }

        User u = new User();
        u.setFirstName(req.getFirstName());
        u.setSecondName(req.getSecondName());
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRoles(Set.copyOf(req.getRoles()));
        userRepo.save(u);

        return ResponseEntity.created(URI.create("/users/" + u.getId()))
                .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        User u = userRepo.findByEmail(req.getEmail())
                .orElse(null);
        if (u == null || !passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.generateToken(u.getEmail());
        JwtResponse resp = new JwtResponse();
        resp.setToken(token);
        return ResponseEntity.ok(resp);
    }
}
