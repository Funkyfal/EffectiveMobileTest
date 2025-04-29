package EffectiveMobile.Test.DTO;
import EffectiveMobile.Test.entities.CardStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CardCreateDto {

    @NotBlank(message = "Card number must not be blank")
    private String number;

    @NotBlank(message = "Owner name must not be blank")
    private String owner;

    @NotNull(message = "Expiration date must not be null")
    @FutureOrPresent(message = "Expiration date must be today or in the future")
    private LocalDate expirationDate;

    @NotNull(message = "Card status must be specified")
    private CardStatus status;

    public CardCreateDto() {}

    public CardCreateDto(String number, String owner, LocalDate expirationDate, CardStatus status) {
        this.number = number;
        this.owner = owner;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }
}

