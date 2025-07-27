package pulse.back.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.response.ResultData;

import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.core.codec.DecodingException;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ResultData<String>> handleCustomException(CustomException e){
        return ResponseEntity.status(e.httpStatusCode())
                .body(ResultData.<String>builder()
                        .body(e.body())
                        .message(e.message())
                        .build()
                );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    protected ResponseEntity<ResultData<String>> handleBaseException(WebExchangeBindException e, ServerHttpResponse response){
        log.debug("DETECTED WebExchangeBindException");

        String message = Optional.of(e.getFieldError().getField() + " : ").orElse("Unknown : ")
                + Optional.ofNullable(e.getFieldError().getDefaultMessage()).orElse("Contact your manager");
        String body = "";

        String code = Optional.ofNullable(e.getFieldError().getCode()).orElse("Unknown");
        if(code.equalsIgnoreCase("Pattern")) {
            message = Optional.of(e.getFieldError().getField() + " : ").orElse("Unknown : ")
                    + ErrorCodes.INVALID_FORMAT.message();
            body = ErrorCodes.INVALID_FORMAT.name();
        }else if(code.equalsIgnoreCase("NotEmpty") || code.equalsIgnoreCase("NotNull")){
            body = ErrorCodes.REQUIRED_REQUEST_BODY_EMPTY.name();
        }else if(code.equalsIgnoreCase("Min") || code.equalsIgnoreCase("Max") || code.equalsIgnoreCase("Size")){
            body = ErrorCodes.LENGTH_MISMATCH.name();
        } else{
            body = ErrorCodes.VALIDATION_CHECK_FOR_BIND_EXCEPTION.name();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultData.<String>builder()
                        .body(body)
                        .message(message)
                        .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ResultData<String>> handleException(ResponseStatusException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResultData.<String>builder()
                        .body(HttpStatus.NOT_FOUND.name())
                        .message("존재하지 않는 페이지 입니다.")
                        .build()
                );
    }

    @ExceptionHandler(ServerWebInputException.class)
    protected ResponseEntity<ResultData<String>> handleException(ServerWebInputException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultData.<String>builder()
                        .body(HttpStatus.BAD_REQUEST.name())
                        .message("올바른 요청이 아닙니다.")
                        .build()
                );
    }

    @ExceptionHandler(DecodingException.class)
    protected ResponseEntity<ResultData<String>> handleException(DecodingException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultData.<String>builder()
                        .body(HttpStatus.BAD_REQUEST.name())
                        .message("올바른 요청이 아닙니다.")
                        .build()
                );
    }

    private List<String> getStackTraceAsList(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}