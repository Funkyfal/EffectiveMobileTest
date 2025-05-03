package EffectiveMobile.Test;

import EffectiveMobile.Test.entities.User;
import EffectiveMobile.Test.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class AdminUserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    private Long userId;

    @BeforeEach
    void initUser() {
        userRepo.deleteAll();

        User u = new User();
        u.setFirstName("Alice");
        u.setSecondName("Smith");
        u.setEmail("alice.smith@example.com");
        u.setPasswordHash(encoder.encode("password"));
        u.setRoles(Set.of("ROLE_USER"));
        userId = userRepo.save(u).getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_updateRoles_delete_user() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alice.smith@example.com"));

        mockMvc.perform(put("/admin/users/{id}/roles", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"ROLE_ADMIN\"]"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/admin/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void access_denied_without_admin() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized());
    }
}
