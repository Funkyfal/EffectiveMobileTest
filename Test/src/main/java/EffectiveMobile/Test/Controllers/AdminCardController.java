package EffectiveMobile.Test.Controllers;

import EffectiveMobile.Test.DTO.CardCreateDto;
import EffectiveMobile.Test.DTO.CardResponseDto;
import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.services.CardService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public Page<CardResponseDto> getAll(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return cardService.listAllCards(status, page, size);
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> create(@Valid @RequestBody CardCreateDto dto) {
        Card card = cardService.addCard(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CardResponseDto(card));
    }

    @PostMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        cardService.blockCardByAdmin(id);
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        cardService.activateCardByAdmin(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cardService.deleteCardByAdmin(id);
    }
}
