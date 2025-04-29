package EffectiveMobile.Test.services;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.repositories.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import EffectiveMobile.Test.entities.Card;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Card addCard(CardCreateDto dto){
        Card card = new Card();
        card.setNumber(dto.getNumber());
        card.setOwner(dto.getOwner());
        card.setStatus(dto.getStatus());
        card.setExpirationDate(dto.getExpirationDate());
        card.setBalance(BigDecimal.valueOf(0.0));

        return cardRepository.save(card);
    }

    public List<Card> getAllCards(){
        return cardRepository.findAll();
    }

    public Optional<Card> getCard(Long id){
        return cardRepository.findById(id);
    }
}
