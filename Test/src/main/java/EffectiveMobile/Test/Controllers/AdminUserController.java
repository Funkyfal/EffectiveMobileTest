package EffectiveMobile.Test.Controllers;

import EffectiveMobile.Test.DTO.UserDto;
import EffectiveMobile.Test.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepo,
                               PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserDto> listAll() {
        return userRepo.findAll().stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<Object> updateRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles
    ) {
        return userRepo.findById(id)
                .map(u -> {
                    u.setRoles(roles);
                    userRepo.save(u);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

