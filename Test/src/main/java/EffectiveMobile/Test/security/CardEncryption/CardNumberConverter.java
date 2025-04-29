package EffectiveMobile.Test.security.CardEncryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CardNumberConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String plainNumber) {
        return CryptoUtil.encrypt(plainNumber);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return CryptoUtil.decrypt(dbData);
    }
}
