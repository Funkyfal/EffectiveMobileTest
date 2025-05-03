package EffectiveMobile.Test.Controllers;

import EffectiveMobile.Test.DTO.CardResponseDto;
import EffectiveMobile.Test.DTO.TransferRequestDto;
import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.services.CardService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/cards")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserCardController {

    private final CardService cardService;

    public UserCardController(CardService cardService){
        this.cardService = cardService;
    }

    @GetMapping
    public Page<CardResponseDto> list(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return cardService.listMyCards(status, page, size);
    }

    @PostMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        cardService.blockMyCard(id);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequestDto dto) throws ChangeSetPersister.NotFoundException {
        cardService.transfer(dto.getFromCardId(), dto.getToCardId(), dto.getAmount());
    }

    @GetMapping("/balance")
    public Map<String, BigDecimal> balance() {
        BigDecimal total = cardService.getMyTotalBalance();
        return Map.of("totalBalance", total);
    }

    @GetMapping("/{card_id}")
    public Optional<Card> getOneCard(@PathVariable("card_id") Long id){
        return cardService.getCard(id);
    }

}
