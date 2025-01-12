package pulse.back.domain.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import pulse.back.domain.s3.service.FileServiceImpl;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
public class FileController {

    private final FileServiceImpl fileServiceImpl;


    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "PULSE-97", summary = "파일 업로드", description = """
            ### [ 설명 ]
            - 파일 업로드를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [FilePart]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> uploadFiles(
            @Parameter(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("업로드 할 파일") FilePart file
    ) {
        log.debug("file : {}", file);
        log.debug("fileName : {}", file.filename());
        log.debug("fileContentType : {}", file.headers().getContentType());
        return fileServiceImpl.uploadFile(file, "");
    }

    /**
     * 파일 다운로드
     * */
    @GetMapping(value = "/download")
    @Operation(operationId = "PULSE-97", summary = "파일 다운로드", description = """
            ### [ 설명 ]
            - 파일 다운로드를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [String fileName]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResponseEntity<ByteArrayResource>> downloadFile(
            @Parameter(description = "다운로드 할 파일의 S3 경로 (예 : folder/example.jpg)")
            @RequestParam(name = "file_name") String fileName
    ) {
        return fileServiceImpl.downloadFile(fileName)
                .map(responseBytes -> {
                    ByteArrayResource resource = new ByteArrayResource(responseBytes.asByteArray());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .contentLength(resource.contentLength())
                            .body(resource);
                });
    }

    /**
     * 파일 삭제
     * */
    @DeleteMapping(value = "/delete")
    @Operation(operationId = "PULSE-97", summary = "파일 삭제", description = """
            ### [ 설명 ]
            - 파일 삭제를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [String fileName]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<ResultCodes>> deleteFile(
            @Parameter(description = "삭제할 파일의 S3 경로 (예: folder/example.jpg)")
            @RequestParam(name = "file_name",  defaultValue = "") String fileName
    ) {
        return fileServiceImpl.deleteFile(fileName);
    }

    /**
     * 파일 목록 조회
     * */
    @GetMapping("/list")
    @Operation(operationId = "PULSE-97", summary = "파일 목록 조회", description = """
            ### [ 설명 ]
            - 파일 목록 조회를 진행합니다.
            <br>
            ### [ 주의사항 ]
            - 최상단 목록을 조회할 경우 prefix 를 비워주세요.
            <br>
            ### [ 요청응답 ]
            ```
            - Request  : [String prefix]
            - Response : [ResultCodes]
            ```
            """)
    public Mono<ResultData<List<String>>> listFiles(
            @RequestParam(name = "prefix", defaultValue = "") String prefix
    ) {
        return fileServiceImpl.listFiles(prefix);
    }
}