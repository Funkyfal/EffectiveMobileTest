package EffectiveMobile.Test.Controllers;

import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.services.CardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
