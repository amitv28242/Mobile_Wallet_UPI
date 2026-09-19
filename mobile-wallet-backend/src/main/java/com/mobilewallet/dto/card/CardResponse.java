package com.mobilewallet.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
	private Long id;
	private String cardNumberMasked;
	private String cardholderName;
	private String expiryMonth;
	private String expiryYear;
	private String issuer;
	private String lastFour;
	private Boolean isDefault;
	private String createdAt;
}
