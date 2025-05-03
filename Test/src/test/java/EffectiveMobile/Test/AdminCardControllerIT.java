package EffectiveMobile.Test;

import EffectiveMobile.Test.entities.User;
import EffectiveMobile.Test.repositories.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class AdminCardControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    @BeforeEach
    void initUser() {
        userRepo.deleteAll();
        User adminOwner = new User();
        adminOwner.setFirstName("John");
        adminOwner.setSecondName("Doe");
        adminOwner.setEmail("john.doe@example.com");
        adminOwner.setPasswordHash(encoder.encode("secret"));
        adminOwner.setRoles(Set.of("ROLE_USER"));
        userRepo.save(adminOwner);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_block_activate_delete_flow() throws Exception {
        String payload = """
            {
              "number": "1111222233334444",
              "owner": "John Doe",
              "expirationDate": "2030-01-01",
              "status": "ACTIVE"
            }
        """;

        MvcResult res = mockMvc.perform(post("/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 4444"))
                .andReturn();

        String body = res.getResponse().getContentAsString();
        Long id = Long.valueOf(JsonPath.read(body, "$.id").toString());

        mockMvc.perform(post("/admin/cards/{id}/block", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/admin/cards/{id}/activate", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/admin/cards/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void access_denied_without_admin() throws Exception {
        mockMvc.perform(get("/admin/cards"))
                .andExpect(status().isUnauthorized());
    }
}

