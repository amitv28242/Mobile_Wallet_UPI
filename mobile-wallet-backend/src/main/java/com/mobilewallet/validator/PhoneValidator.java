// FILE: src/main/java/com/mobilewallet/validator/PhoneValidator.java
package com.mobilewallet.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return true;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}