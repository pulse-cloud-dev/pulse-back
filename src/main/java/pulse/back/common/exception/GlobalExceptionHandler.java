package pulse.back.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pulse.back.common.response.ResultData;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

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

//    @ExceptionHandler(NoResourceFoundException.class)
//    protected ResponseEntity<ResultData<String>> handleException(NoResourceFoundException e){
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ResultData.<String>builder()
//                        .body(HttpStatus.NOT_FOUND.name())
//                        .message("존재하지 않는 페이지 입니다.")
//                        .build()
//                );
//    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    protected ResponseEntity<ResultData<String>> handleException(HttpMessageNotReadableException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ResultData.<String>builder()
//                        .body(HttpStatus.BAD_REQUEST.name())
//                        .message("올바른 요청이 아닙니다.")
//                        .build()
//                );
//    }

//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    protected ResponseEntity<ResultData<String>> handleException(MethodArgumentTypeMismatchException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ResultData.<String>builder()
//                        .body(HttpStatus.BAD_REQUEST.name())
//                        .message(ErrorCodes.INVALID_REQUEST.message())
//                        .build()
//                );
//    }
//
//
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ResultData<String>> handleException(Exception e){
//        LocalDateTime now = LocalDateTime.now();
//        log.error("Exception",e);
//
//        { //에러 로그 저장
//            StringWriter stringWriter = new StringWriter();
//            PrintWriter printWriter = new PrintWriter(stringWriter);
//            e.printStackTrace(printWriter);
//            String errorData = stringWriter.toString();
//
//            errorLogAdminApiRepository.insert(ErrorLogAdminApi.ErrorLogAdminApiBuilder
//                    .anErrorLogGateway()
//                    .withId(new ObjectId())
//                    .withData(errorData)
//                    .withCreatedAt(now)
//                    .build()
//            );
//        }
//
//        int statusCode = 500;
//        try{
//            statusCode = HttpStatus.valueOf(e.getMessage()).value();
//        }catch (Exception ignore){}
//
//        return ResponseEntity.status(statusCode)
//                .body(ResultData.<String>builder()
//                        .body(HttpStatus.valueOf(statusCode).name())
//                        .message(e.getMessage())
//                        .build()
//                );
//    }

}
