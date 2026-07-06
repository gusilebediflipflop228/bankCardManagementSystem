package api.bank.service;

import api.bank.dto.TransferRequest;
import api.bank.entity.*;
import api.bank.exception.*;
import api.bank.repository.CardRepository;
import api.bank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransferService transferService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .username("ivan")
                .password("encoded")
                .role(Role.USER)
                .build();
    }

    private Card createTestCard(User owner, long id, BigDecimal balance) {
        return Card.builder()
                .id(id)
                .cardNumber("encrypted")
                .cardholder("Ivan Ivanov")
                .expiryDate(LocalDate.of(2028, 12, 31))
                .status(CardStatus.ACTIVE)
                .balance(balance)
                .owner(owner)
                .build();
    }

    @Test
    void transfer_success() {
        User user = createTestUser();
        Card fromCard = createTestCard(user, 1L, new BigDecimal("1000.00"));
        Card toCard = createTestCard(user, 2L, new BigDecimal("500.00"));

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("200.00"));
        transferService.transfer(request, "ivan");

        assertEquals(new BigDecimal("800.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("700.00"), toCard.getBalance());
    }

    @Test
    void transfer_insufficientFunds() {
        User user = createTestUser();
        Card fromCard = createTestCard(user, 1L, new BigDecimal("100.00"));
        Card toCard = createTestCard(user, 2L, new BigDecimal("500.00"));

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("500.00"));
        assertThrows(InsufficientFundsException.class, () -> transferService.transfer(request, "ivan"));
    }

    @Test
    void transfer_sameCard() {
        User user = createTestUser();
        Card card = createTestCard(user, 1L, new BigDecimal("1000.00"));

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        TransferRequest request = new TransferRequest(1L, 1L, new BigDecimal("100.00"));
        assertThrows(SameCardTransferException.class, () -> transferService.transfer(request, "ivan"));
    }

    @Test
    void transfer_notOwner() {
        User user = createTestUser();
        User other = User.builder().id(2L).username("petr").password("x").role(Role.USER).build();
        Card fromCard = createTestCard(user, 1L, new BigDecimal("1000.00"));
        Card toCard = createTestCard(other, 2L, new BigDecimal("500.00"));

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));
        assertThrows(CardNotFoundException.class, () -> transferService.transfer(request, "ivan"));
    }

    @Test
    void transfer_cardNotActive() {
        User user = createTestUser();
        Card fromCard = createTestCard(user, 1L, new BigDecimal("1000.00"));
        fromCard.setStatus(CardStatus.BLOCKED);
        Card toCard = createTestCard(user, 2L, new BigDecimal("500.00"));

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));
        assertThrows(CardNotActiveException.class, () -> transferService.transfer(request, "ivan"));
    }
}
