package pulse.back.domain.chat.dto.response;

import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.exception.CustomException;

public record ErrorMessage(
        String body,
        String message
) {
    public static ErrorMessage fromException(CustomException e) {
        return new ErrorMessage(e.body(), e.message());
    }

    public static ErrorMessage fromErrorCodes(ErrorCodes errorCodes) {
        return new ErrorMessage(errorCodes.name(), errorCodes.message());
    }
}
