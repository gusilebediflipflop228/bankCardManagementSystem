package api.bank.repository;

import api.bank.entity.Card;
import api.bank.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);
}
