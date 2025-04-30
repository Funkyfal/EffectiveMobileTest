package EffectiveMobile.Test.Controllers;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService){
        this.cardService = cardService;
    }

    @GetMapping("/{card_id}")
    public Optional<Card> getOneCard(@PathVariable("card_id") Long id){
        return cardService.getCard(id);
    }

    @GetMapping("/cards")
    public List<Card> getAllCards(){
        return cardService.getAllCards();
    }

    @PostMapping("/new")
    public Card addNewCard(@RequestBody CardCreateDto dto){
        return cardService.addCard(dto);
    }

    @DeleteMapping("/{card_id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("card_id") Long id){
        return cardService.deleteCard(id);
    }
}
