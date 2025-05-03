package EffectiveMobile.Test.services;

import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.repositories.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardExpiryScheduler {

    private final CardRepository cardRepository;

    public CardExpiryScheduler(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireCards() {
        List<Card> all = cardRepository.findAll();
        LocalDate today = LocalDate.now();
        for (Card c : all) {
            if (c.getStatus() != CardStatus.EXPIRED
                    && c.getExpirationDate().isBefore(today)) {
                c.setStatus(CardStatus.EXPIRED);
            }
        }
        cardRepository.flush();
    }
}
