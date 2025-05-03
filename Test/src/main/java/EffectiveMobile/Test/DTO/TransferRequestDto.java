package EffectiveMobile.Test.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransferRequestDto {
    @NotNull(message = "Source card ID must be provided")
    private Long fromCardId;

    @NotNull(message = "Target card ID must be provided")
    private Long toCardId;

    @NotNull(message = "Amount must be provided")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    public @NotNull Long getFromCardId() {
        return fromCardId;
    }

    public void setFromCardId(@NotNull Long fromCardId) {
        this.fromCardId = fromCardId;
    }

    public @NotNull Long getToCardId() {
        return toCardId;
    }

    public void setToCardId(@NotNull Long toCardId) {
        this.toCardId = toCardId;
    }

    public @NotNull @Positive BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @Positive BigDecimal amount) {
        this.amount = amount;
    }
}
