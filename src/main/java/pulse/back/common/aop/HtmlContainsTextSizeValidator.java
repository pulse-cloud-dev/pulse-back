package pulse.back.common.aop;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HtmlContainsTextSizeValidator implements ConstraintValidator<HtmlContainsTextSize, String> {

    private int min;
    private int max;

    @Override
    public void initialize(HtmlContainsTextSize annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // HTML 태그 제거
        String plainText = value.replaceAll("<[^>]*>", "");
        int length = plainText.length();

        return length >= min && length <= max;
    }
}
