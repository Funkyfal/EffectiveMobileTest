package EffectiveMobile.Test.repositories;

import EffectiveMobile.Test.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long>{
}
