package EffectiveMobile.Test.services;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.DTO.CardResponseDto;
import EffectiveMobile.Test.Exceptions.InvalidCardDataException;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.repositories.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import EffectiveMobile.Test.entities.Card;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final AuthService authService;

    public CardService(CardRepository cardRepository, AuthService authService) {
        this.cardRepository = cardRepository;
        this.authService = authService;
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

    public ResponseEntity<Void> deleteCard(Long id){
        boolean exists = cardRepository.findById(id).isPresent();
        if(exists){
            cardRepository.deleteById(id);
        }

        return exists
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    public Page<CardResponseDto> listMyCards(CardStatus status, int page, int size) {
        String me = authService.getCurrentUserEmail();
        Pageable pg = PageRequest.of(page, size, Sort.by("expirationDate").descending());
        Page<Card> cards = (status != null)
                ? cardRepository.findByOwnerAndStatus(me, status, pg)
                : cardRepository.findByOwner(me, pg);
        return cards.map(CardResponseDto::new);
    }

    @Transactional
    public void blockMyCard(Long cardId) throws ChangeSetPersister.NotFoundException {
        String me = authService.getCurrentUserEmail();
        Card card = cardRepository.findById(cardId)
                .filter(c -> c.getOwner().equals(me))
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) throws ChangeSetPersister.NotFoundException {
        String me = authService.getCurrentUserEmail();
        Card from = cardRepository.findById(fromId)
                .filter(c -> c.getOwner().equals(me))
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        Card to = cardRepository.findById(toId)
                .filter(c -> c.getOwner().equals(me))
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InvalidCardDataException("Insufficient funds");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);
    }

    public BigDecimal getMyTotalBalance() {
        String me = authService.getCurrentUserEmail();
        return cardRepository.findByOwner(me, Pageable.unpaged())
                .stream()
                .map(Card::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Page<CardResponseDto> listAllCards(CardStatus status, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("expirationDate").descending());
        Page<Card> cards = (status != null)
                ? cardRepository.findByStatus(status, pg)
                : cardRepository.findAll(pg);
        return cards.map(CardResponseDto::new);
    }

    @Transactional
    public void blockCardByAdmin(Long id) throws ChangeSetPersister.NotFoundException {
        Card card = cardRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void activateCardByAdmin(Long id) throws ChangeSetPersister.NotFoundException {
        Card card = cardRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Transactional
    public void deleteCardByAdmin(Long id) {
        cardRepository.deleteById(id);
    }
}
