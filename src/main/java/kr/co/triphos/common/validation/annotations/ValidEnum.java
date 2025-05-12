package kr.co.triphos.common.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kr.co.triphos.common.validation.validator.EnumValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidEnum {

    String message() default "입력한 값이 올바르지 않습니다. 정확한 항목을 선택했는지 확인해 주세요. 문제가 지속되면 관리자에게 문의하세요.";

    Class<?>[] groups() default {};

    Class<? extends Enum<?>> enumClass();

    Class<? extends Payload>[] payload() default {};

    boolean ignoreCase() default false;
}
