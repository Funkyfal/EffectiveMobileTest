package EffectiveMobile.Test.DTO;

import EffectiveMobile.Test.entities.Card;
import EffectiveMobile.Test.entities.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardResponseDto {
    private Long id;
    private String maskedNumber;
    private String owner;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;

    public CardResponseDto(Card card) {
        this.id             = card.getId();
        this.maskedNumber   = card.getMaskedNumber();
        this.owner          = card.getOwner();
        this.expirationDate = card.getExpirationDate();
        this.status         = card.getStatus();
        this.balance        = card.getBalance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
