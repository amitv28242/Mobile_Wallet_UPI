// FILE: src/main/java/com/mobilewallet/util/ValidationUtil.java
package com.mobilewallet.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");
    
    private static final Pattern IFSC_PATTERN = 
            Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    
    private static final Pattern CARD_NUMBER_PATTERN = 
            Pattern.compile("^\\d{15,16}$");
    
    private static final Pattern CVV_PATTERN = 
            Pattern.compile("^\\d{3,4}$");
    
    private static final Pattern GST_PATTERN = 
            Pattern.compile("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidIFSC(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc).matches();
    }

    public static boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null && CARD_NUMBER_PATTERN.matcher(cardNumber).matches() &&
                luhnCheck(cardNumber);
    }

    public static boolean isValidCVV(String cvv) {
        return cvv != null && CVV_PATTERN.matcher(cvv).matches();
    }

    public static boolean isValidGST(String gst) {
        return gst != null && GST_PATTERN.matcher(gst).matches();
    }

    private static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static boolean isAmountValid(java.math.BigDecimal amount) {
        return amount != null && amount.compareTo(java.math.BigDecimal.ZERO) > 0 &&
                amount.scale() <= 2;
    }

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        int visibleDigits = 4;
        String masked = cardNumber.substring(0, cardNumber.length() - visibleDigits)
                .replaceAll("\\d", "*");
        return masked + cardNumber.substring(cardNumber.length() - visibleDigits);
    }

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 6) {
            return "****";
        }
        int visibleDigits = 4;
        String masked = accountNumber.substring(0, accountNumber.length() - visibleDigits)
                .replaceAll("\\d", "*");
        return masked + accountNumber.substring(accountNumber.length() - visibleDigits);
    }
}