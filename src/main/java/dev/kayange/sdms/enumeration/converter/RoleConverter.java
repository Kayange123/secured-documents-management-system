package dev.kayange.sdms.enumeration.converter;

import dev.kayange.sdms.enumeration.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Authority, String> {

    @Override
    public String convertToDatabaseColumn(Authority authority) {
        if (authority==null) return null;
        return authority.getValue();
    }

    @Override
    public Authority convertToEntityAttribute(String dbData) {
        if(dbData==null) return null;
        return Stream.of(Authority.values()).filter(authority -> authority.getValue().equals(dbData)).findFirst()
                .orElseThrow(()-> new IllegalStateException("Invalid Authority"));
    }
}
