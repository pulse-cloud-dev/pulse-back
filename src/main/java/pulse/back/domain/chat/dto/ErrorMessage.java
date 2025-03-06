package pulse.back.domain.chat.dto;

import pulse.back.common.enums.ErrorCodes;

public record ErrorMessage(
        String body,
        String message
) {
    public static ErrorMessage fromErrorCodes(ErrorCodes e) {
        return new ErrorMessage(e.name(), e.message());
    }
}
