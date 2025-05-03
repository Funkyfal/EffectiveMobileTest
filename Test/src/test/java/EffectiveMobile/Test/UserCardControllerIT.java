package EffectiveMobile.Test;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.DTO.TransferRequestDto;
import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.entities.User;
import EffectiveMobile.Test.repositories.CardRepository;
import EffectiveMobile.Test.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class UserCardControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private CardRepository cardRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper mapper;

    private final String firstName = "Bob";
    private final String secondName = "Jones";
    private final String email = "bob.jones@example.com";
    private final String ownerName = firstName + " " + secondName;

    @BeforeEach
    void setUp() {
        cardRepo.deleteAll();
        userRepo.deleteAll();

        User u = new User();
        u.setFirstName(firstName);
        u.setSecondName(secondName);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode("secret"));
        u.setRoles(Set.of("ROLE_USER"));
        userRepo.save(u);
    }

    @Test
    void unauthorized_without_user() throws Exception {
        mockMvc.perform(get("/user/cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void flow_list_balance_transfer_and_block() throws Exception {
        CardCreateDto dto1 = new CardCreateDto(
                "0000111122223333", ownerName,
                LocalDate.now().plusYears(2), CardStatus.ACTIVE
        );
        CardCreateDto dto2 = new CardCreateDto(
                "4444555566667777", ownerName,
                LocalDate.now().plusYears(2), CardStatus.ACTIVE
        );

        mockMvc.perform(post("/admin/cards")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/admin/cards")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/user/cards")
                        .with(user(ownerName).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/user/cards/balance")
                        .with(user(ownerName).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(0));

        List<Card> cards = cardRepo.findByOwner(ownerName, Pageable.unpaged()).getContent();
        Card first = cards.get(0);
        first.setBalance(BigDecimal.valueOf(100));
        cardRepo.save(first);

        Card second = cards.get(1);
        TransferRequestDto tr = new TransferRequestDto();
        tr.setFromCardId(first.getId());
        tr.setToCardId(second.getId());
        tr.setAmount(BigDecimal.valueOf(50));

        mockMvc.perform(post("/user/cards/transfer")
                        .with(user(ownerName).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tr)))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/user/cards/{id}/block", second.getId())
                        .with(user(ownerName).roles("USER")))
                .andExpect(status().isNoContent());
    }
}
