package EffectiveMobile.Test.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Card {
    @Id
    private Long Id;
    private String owner;
    private String expiration_date;
    private String state;
    private Float balance;
}
