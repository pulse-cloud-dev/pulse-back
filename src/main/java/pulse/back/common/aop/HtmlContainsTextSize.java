package pulse.back.common.aop;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

// html 태그가 포함된 텍스트의 길이를 검증하는 어노테이션
@Documented
@Constraint(validatedBy = HtmlContainsTextSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlContainsTextSize {
    String message() default "텍스트 길이가 {min}자 이상 {max}자 이하여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 0;
    int max() default Integer.MAX_VALUE;
}

