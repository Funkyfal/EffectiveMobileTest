package EffectiveMobile.Test;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.Exceptions.InvalidCardDataException;
import EffectiveMobile.Test.entities.BeanUtil;
import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.entities.User;
import EffectiveMobile.Test.repositories.CardRepository;
import EffectiveMobile.Test.repositories.UserRepository;
import EffectiveMobile.Test.services.AuthService;
import EffectiveMobile.Test.services.CardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationContext;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private CardService cardService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        new BeanUtil().setApplicationContext(applicationContext);
        when(applicationContext.getBean(UserRepository.class))
                .thenReturn(userRepository);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(new User()));
    }

    @Test
    void getAllCards_delegatesToRepository() {
        List<Card> list = List.of(new Card(), new Card());
        when(cardRepository.findAll()).thenReturn(list);

        var result = cardService.getAllCards();
        assertSame(list, result);
        verify(cardRepository).findAll();
    }

    @Test
    void getCard_existing() {
        Card c = new Card();
        when(cardRepository.findById(42L)).thenReturn(Optional.of(c));

        var opt = cardService.getCard(42L);
        assertTrue(opt.isPresent());
        assertSame(c, opt.get());
    }

    @Test
    void deleteCard_found() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(new Card()));

        var resp = cardService.deleteCard(1L);
        assertEquals(204, resp.getStatusCodeValue());
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void deleteCard_notFound() {
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());

        var resp = cardService.deleteCard(2L);
        assertEquals(404, resp.getStatusCodeValue());
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    void listMyCards_withStatus() {
        Card c = new Card();
        c.setNumber("9999000011112222");
        c.setExpirationDate(LocalDate.now().plusDays(1));
        c.setStatus(CardStatus.BLOCKED);
        c.setBalance(BigDecimal.ZERO);
        c.setOwner("Me Example");

        when(authService.getCurrentUserEmail()).thenReturn("me@example.com");
        when(cardRepository.findByOwnerAndStatus(
                eq("me@example.com"), eq(CardStatus.BLOCKED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c)));

        var page = cardService.listMyCards(CardStatus.BLOCKED, 0, 5);

        assertEquals(1, page.getTotalElements());
        assertEquals("**** **** **** 2222", page.getContent().get(0).getMaskedNumber());
        verify(cardRepository).findByOwnerAndStatus(
                eq("me@example.com"), eq(CardStatus.BLOCKED), any());
    }

    @Test
    void listMyCards_withoutStatus() {
        Card c = new Card();
        c.setNumber("1234123412341234");
        c.setExpirationDate(LocalDate.now().plusDays(1));
        c.setStatus(CardStatus.ACTIVE);
        c.setBalance(BigDecimal.ZERO);
        c.setOwner("User Test");

        when(authService.getCurrentUserEmail()).thenReturn("user@test.com");
        when(cardRepository.findByOwner(eq("user@test.com"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c)));

        var page = cardService.listMyCards(null, 1, 10);

        assertEquals(1, page.getTotalElements());
        verify(cardRepository).findByOwner(eq("user@test.com"), any());
    }

    @Test
    void blockMyCard_success() throws Exception {
        Card c = new Card();
        c.setNumber("2222333344445555");
        c.setExpirationDate(LocalDate.now().plusMonths(1));
        c.setStatus(CardStatus.ACTIVE);
        c.setBalance(BigDecimal.ZERO);
        c.setOwner("Me Me");

        when(authService.getCurrentUserEmail()).thenReturn("Me Me");
        when(cardRepository.findById(7L)).thenReturn(Optional.of(c));

        cardService.blockMyCard(7L);

        assertEquals(CardStatus.BLOCKED, c.getStatus());
        verify(cardRepository).save(c);
    }

    @Test
    void blockMyCard_notOwner_throws() {
        Card c = new Card();
        c.setNumber("1111222233334444");
        c.setExpirationDate(LocalDate.now().plusYears(1));
        c.setStatus(CardStatus.ACTIVE);
        c.setBalance(BigDecimal.ZERO);
        c.setOwner("Other Other");

        when(authService.getCurrentUserEmail()).thenReturn("me");
        when(cardRepository.findById(8L)).thenReturn(Optional.of(c));

        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> cardService.blockMyCard(8L));
    }

    @Test
    void transfer_success() throws Exception {
        Card from = new Card();
        from.setNumber("0000111122223333");
        from.setExpirationDate(LocalDate.now().plusYears(1));
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(BigDecimal.valueOf(100));
        from.setOwner("U U");

        Card to = new Card();
        to.setNumber("4444555566667777");
        to.setExpirationDate(LocalDate.now().plusYears(1));
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(BigDecimal.valueOf(10));
        to.setOwner("U U");

        when(authService.getCurrentUserEmail()).thenReturn("U U");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        cardService.transfer(1L, 2L, BigDecimal.valueOf(40));

        assertEquals(BigDecimal.valueOf(60), from.getBalance());
        assertEquals(BigDecimal.valueOf(50), to.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transfer_insufficientFunds_throws() {
        Card from = new Card();
        from.setNumber("0000111122223333");
        from.setExpirationDate(LocalDate.now().plusYears(1));
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(BigDecimal.TEN);
        from.setOwner("U U");

        Card to = new Card();
        to.setNumber("4444555566667777");
        to.setExpirationDate(LocalDate.now().plusYears(1));
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(BigDecimal.ZERO);
        to.setOwner("U U");

        when(authService.getCurrentUserEmail()).thenReturn("U U");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(InvalidCardDataException.class,
                () -> cardService.transfer(1L, 2L, BigDecimal.valueOf(20)));
    }

    @Test
    void getMyTotalBalance() {
        Card a = new Card(); a.setBalance(BigDecimal.valueOf(1));
        Card b = new Card(); b.setBalance(BigDecimal.valueOf(2));

        when(authService.getCurrentUserEmail()).thenReturn("me");
        when(cardRepository.findByOwner(eq("me"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(a, b)));

        BigDecimal total = cardService.getMyTotalBalance();
        assertEquals(BigDecimal.valueOf(3), total);
    }

    @Test
    void listAllCards_andAdminMethods() throws Exception {
        Card c = new Card();
        c.setNumber("1111000022223333");
        c.setExpirationDate(LocalDate.now().plusDays(1));
        c.setStatus(CardStatus.ACTIVE);
        c.setBalance(BigDecimal.ZERO);
        c.setOwner("A B");

        when(cardRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c)));
        when(cardRepository.findByStatus(eq(CardStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c)));

        var all1 = cardService.listAllCards(null, 0, 5);
        assertEquals(1, all1.getTotalElements());

        var all2 = cardService.listAllCards(CardStatus.ACTIVE, 0, 5);
        assertEquals(1, all2.getTotalElements());

        when(cardRepository.findById(5L)).thenReturn(Optional.of(c));
        cardService.blockCardByAdmin(5L);
        assertEquals(CardStatus.BLOCKED, c.getStatus());

        cardService.activateCardByAdmin(5L);
        assertEquals(CardStatus.ACTIVE, c.getStatus());

        cardService.deleteCardByAdmin(5L);
        verify(cardRepository).deleteById(5L);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }
}
