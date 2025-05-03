package EffectiveMobile.Test.DTO;
import EffectiveMobile.Test.entities.CardStatus;
import EffectiveMobile.Test.services.CardService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class CardCreateDto {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Number must be exactly 16 digits")
    private String number;

    @NotBlank
    private String owner;

    @NotNull
    @FutureOrPresent(message = "Expiration date must be today or later")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @NotNull
    private CardStatus status;

    public CardCreateDto() {}

    public CardCreateDto(String visa, String usd, LocalDate localDate) {}

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

