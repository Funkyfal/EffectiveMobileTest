package EffectiveMobile.Test.entities;

import EffectiveMobile.Test.Exceptions.InvalidCardDataException;
import EffectiveMobile.Test.repositories.UserRepository;
import EffectiveMobile.Test.security.CardEncryption.CardNumberConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CardNumberConverter.class)
    @Column(name = "encrypted_number", nullable = false)
    private String number;

    @Column(nullable = false)
    private String owner;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

    public String getMaskedNumber() {
        String plain = getNumber();
        if (plain.length() >= 4) {
            return "**** **** **** " + plain.substring(plain.length() - 4);
        }
        return plain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        String digits = number.replaceAll("\\s+", "");
        if (!digits.matches("\\d{16}")) {
            throw new InvalidCardDataException("Card number must be exactly 16 digits");
        }
        this.number = digits;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (owner == null) {
            throw new InvalidCardDataException("Owner must be provided");
        }
        String trimmed = owner.trim().replaceAll("\\s{2,}", " ");
        String[] parts = trimmed.split(" ");
        if (parts.length != 2 ||
                !parts[0].matches("[A-Za-z]+") ||
                !parts[1].matches("[A-Za-z]+")) {
            throw new InvalidCardDataException(
                    "Owner must be two words, English letters only, e.g. \"John Doe\"");
        }
        UserRepository repo = BeanUtil.getBean(UserRepository.class);
        boolean exists = repo.findByEmail(parts[0].toLowerCase()+"."+parts[1].toLowerCase()+"@example.com")
                .isPresent();
        if (!exists) {
            throw new InvalidCardDataException("User \"" + trimmed + "\" not found");
        }

        this.owner = trimmed;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new InvalidCardDataException("Expiration date must be provided");
        }
        this.expirationDate = expirationDate;
        if (expirationDate.isBefore(LocalDate.now())) {
            this.status = CardStatus.EXPIRED;
        }
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        if (status == null) {
            throw new InvalidCardDataException("Card status must be specified");
        }
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
