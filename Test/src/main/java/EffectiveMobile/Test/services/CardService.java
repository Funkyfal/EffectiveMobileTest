package EffectiveMobile.Test.services;

import EffectiveMobile.Test.repositories.CardRepository;
import org.springframework.stereotype.Service;
import EffectiveMobile.Test.entities.Card;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository){
        this.cardRepository = cardRepository;
    }

    public List<Card> getAllCards(){
        return cardRepository.findAll();
    }

    public Optional<Card> getCard(Long id){
        return cardRepository.findById(id);
    }
}
