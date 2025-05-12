package kr.co.triphos.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.triphos.common.validation.annotations.ValidEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Set<String> acceptedValues;
    private boolean ignoreCase;

    @Override
    public void initialize(ValidEnum annotation) {
        ignoreCase = annotation.ignoreCase();
        Enum<?>[] enumConstants = annotation.enumClass().getEnumConstants();
        acceptedValues = Arrays.stream(enumConstants)
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return ignoreCase
                ? acceptedValues.stream().anyMatch(v -> v.equalsIgnoreCase(value))
                : acceptedValues.contains(value);
    }

}
