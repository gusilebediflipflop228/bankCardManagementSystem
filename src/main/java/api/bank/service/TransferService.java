package api.bank.service;

import api.bank.dto.TransferRequest;
import api.bank.entity.Card;
import api.bank.entity.CardStatus;
import api.bank.entity.User;
import api.bank.exception.*;
import api.bank.repository.CardRepository;
import api.bank.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private static final int MAX_RETRIES = 3;

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void transfer(TransferRequest request, String username) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                doTransfer(request, username);
                return;
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw new InsufficientFundsException("Не удалось выполнить перевод из-за параллельных операций. Попробуйте позже.");
                }
            }
        }
    }

    private void doTransfer(TransferRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта-отправитель с ID " + request.getFromCardId() + " не найдена"));

        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта-получатель с ID " + request.getToCardId() + " не найдена"));

        if (!fromCard.getOwner().getId().equals(user.getId())) {
            throw new CardNotFoundException("Карта-отправитель с ID " + request.getFromCardId() + " не найдена");
        }

        if (!toCard.getOwner().getId().equals(user.getId())) {
            throw new CardNotFoundException("Карта-получатель с ID " + request.getToCardId() + " не найдена");
        }

        if (request.getFromCardId().equals(request.getToCardId())) {
            throw new SameCardTransferException("Нельзя перевести на ту же карту");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Карта-отправитель не активна");
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException("Карта-получатель не активна");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на карте-отправителе");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
}
