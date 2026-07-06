package api.bank.service;

import api.bank.dto.CardCreateRequest;
import api.bank.dto.CardResponse;
import api.bank.entity.*;
import api.bank.exception.*;
import api.bank.repository.CardRepository;
import api.bank.repository.UserRepository;
import api.bank.util.CardUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardUtil cardUtil;

    @InjectMocks
    private CardService cardService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .username("ivan")
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    private Card createTestCard(User owner) {
        return Card.builder()
                .id(1L)
                .cardNumber("encrypted-number")
                .cardholder("Ivan Ivanov")
                .expiryDate(LocalDate.of(2028, 12, 31))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .owner(owner)
                .build();
    }

    @Test
    void createCard_success() {
        User owner = createTestUser();
        CardCreateRequest request = new CardCreateRequest("1234567890123456", "Ivan Ivanov", LocalDate.of(2028, 12, 31), 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(cardUtil.encryptCardNumber("1234567890123456")).thenReturn("encrypted-number");
        when(cardUtil.maskCardNumber("1234567890123456")).thenReturn("**** **** **** 3456");
        when(cardUtil.decryptCardNumber("encrypted-number")).thenReturn("1234567890123456");
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card card = inv.getArgument(0);
            card.setId(1L);
            return card;
        });

        CardResponse response = cardService.createCard(request);

        assertNotNull(response.getId());
        assertEquals("Ivan Ivanov", response.getCardholder());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void blockCard_success() {
        User owner = createTestUser();
        Card card = createTestCard(owner);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(owner));
        when(cardUtil.maskCardNumber(anyString())).thenReturn("**** **** **** 3456");
        when(cardUtil.decryptCardNumber("encrypted-number")).thenReturn("1234567890123456");
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardResponse response = cardService.blockCard(1L, "ivan");

        assertEquals(CardStatus.BLOCKED, response.getStatus());
    }

    @Test
    void blockCard_alreadyBlocked() {
        User owner = createTestUser();
        Card card = createTestCard(owner);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(owner));

        assertThrows(CardAlreadyBlockedException.class, () -> cardService.blockCard(1L, "ivan"));
    }

    @Test
    void getBalance_success() {
        User owner = createTestUser();
        Card card = createTestCard(owner);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(owner));

        BigDecimal balance = cardService.getBalance(1L, "ivan");

        assertEquals(new BigDecimal("1000.00"), balance);
    }

    @Test
    void getBalance_notOwner() {
        User owner = createTestUser();
        User other = User.builder().id(2L).username("petr").password("x").role(Role.USER).build();
        Card card = createTestCard(owner);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("petr")).thenReturn(Optional.of(other));

        assertThrows(CardNotFoundException.class, () -> cardService.getBalance(1L, "petr"));
    }

    @Test
    void cardNotFound() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(999L, "ivan"));
    }
}
