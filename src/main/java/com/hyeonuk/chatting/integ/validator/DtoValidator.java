package com.hyeonuk.chatting.integ.validator;

import com.hyeonuk.chatting.integ.dto.BaseDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DtoValidator implements ConstraintValidator<DtoValid, BaseDto> {
    @Override
    public boolean isValid(BaseDto value, ConstraintValidatorContext context) {
        return value.validate();
    }

    @Override
    public void initialize(DtoValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
