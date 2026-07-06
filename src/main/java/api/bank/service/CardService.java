package api.bank.service;

import api.bank.dto.*;
import api.bank.entity.Card;
import api.bank.entity.CardStatus;
import api.bank.entity.User;
import api.bank.exception.*;
import api.bank.repository.CardRepository;
import api.bank.repository.UserRepository;
import api.bank.util.CardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardUtil cardUtil;

    public PageResponse<CardResponse> getAllCards(Pageable pageable, CardStatus status) {
        Page<Card> page = (status != null)
                ? cardRepository.findByStatus(status, pageable)
                : cardRepository.findAll(pageable);
        return toPageResponse(page);
    }

    public PageResponse<CardResponse> getUserCards(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        Page<Card> page = cardRepository.findByOwnerId(userId, pageable);
        return toPageResponse(page);
    }

    public CardResponse getCardById(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта с ID " + id + " не найдена"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!user.getRole().name().equals("ADMIN") && !card.getOwner().getId().equals(user.getId())) {
            throw new CardNotFoundException("Карта с ID " + id + " не найдена");
        }

        return toResponse(card);
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("Владелец с ID " + request.getOwnerId() + " не найден"));

        String encryptedNumber = cardUtil.encryptCardNumber(request.getCardNumber());

        Card card = Card.builder()
                .cardNumber(encryptedNumber)
                .cardholder(request.getCardholder())
                .expiryDate(request.getExpiryDate())
                .status(CardStatus.ACTIVE)
                .balance(java.math.BigDecimal.ZERO)
                .owner(owner)
                .build();

        return toResponse(cardRepository.save(card));
    }

    @Transactional
    public CardResponse blockCard(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта с ID " + id + " не найдена"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!user.getRole().name().equals("ADMIN") && !card.getOwner().getId().equals(user.getId())) {
            throw new CardNotFoundException("Карта с ID " + id + " не найдена");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardAlreadyBlockedException("Карта уже заблокирована");
        }

        card.setStatus(CardStatus.BLOCKED);
        return toResponse(cardRepository.save(card));
    }

    @Transactional
    public CardResponse activateCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта с ID " + id + " не найдена"));

        card.setStatus(CardStatus.ACTIVE);
        return toResponse(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException("Карта с ID " + id + " не найдена");
        }
        cardRepository.deleteById(id);
    }

    public java.math.BigDecimal getBalance(Long cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта с ID " + cardId + " не найдена"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!card.getOwner().getId().equals(user.getId())) {
            throw new CardNotFoundException("Карта с ID " + cardId + " не найдена");
        }

        return card.getBalance();
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                cardUtil.maskCardNumber(cardUtil.decryptCardNumber(card.getCardNumber())),
                card.getCardholder(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance(),
                card.getOwner().getId()
        );
    }

    private PageResponse<CardResponse> toPageResponse(Page<Card> page) {
        return new PageResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
