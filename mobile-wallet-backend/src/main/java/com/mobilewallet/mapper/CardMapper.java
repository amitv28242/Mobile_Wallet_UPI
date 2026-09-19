// FILE: src/main/java/com/mobilewallet/mapper/CardMapper.java
package com.mobilewallet.mapper;

import com.mobilewallet.dto.card.CardRequest;
import com.mobilewallet.dto.card.CardResponse;
import com.mobilewallet.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cardNumberMasked", ignore = true)
    @Mapping(target = "issuer", ignore = true)
    @Mapping(target = "lastFour", ignore = true)
    @Mapping(target = "encryptedData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Card toEntity(CardRequest request);

    @Mapping(source = "cardNumberMasked", target = "cardNumberMasked")
    @Mapping(source = "isDefault", target = "isDefault")
    CardResponse toResponse(Card card);

    void updateEntity(@MappingTarget Card card, CardRequest request);
}