package EffectiveMobile.Test.repositories;

import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long>{
    Page<Card> findByOwnerAndStatus(String owner, CardStatus status, Pageable pageable);

    Page<Card> findByOwner(String owner, Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

}
