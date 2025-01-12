package pulse.back.domain.s3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pulse.back.common.enums.ErrorCodes;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.exception.CustomException;
import pulse.back.common.response.ResultData;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3AsyncClient s3AsyncClient;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * 파일 업로드
     */
    @Override
    public Mono<ResultData<ResultCodes>> uploadFile(FilePart filePart, String path) {
        return filePart.content()
                .reduce(ByteBuffer.allocate(0), (prev, current) -> {
                    ByteBuffer combined = ByteBuffer.allocate(prev.remaining() + current.readableByteCount());
                    combined.put(prev);
                    byte[] bytes = new byte[current.readableByteCount()];
                    current.read(bytes);
                    combined.put(bytes);
                    return combined;
                })
                .flatMap(buffer -> {
                    String key = path + filePart.filename();
                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

                    return Mono.fromFuture(() -> s3AsyncClient.putObject(request, AsyncRequestBody.fromByteBuffer(buffer)))
                            .map(response -> new ResultData<>(ResultCodes.SUCCESS, "파일 업로드에 성공했습니다."))
                            .onErrorMap(e -> {
                                log.error("파일 업로드 실패: {}", e.getMessage());
                                return new CustomException(ErrorCodes.FILE_UPLOAD_FAILED,
                                        "파일 업로드에 실패하였습니다. (파일명 : " + filePart.filename() + ")");
                            });
                });
    }

    /**
     * 파일 다운로드
     */
    @Override
    public Mono<ResponseBytes<GetObjectResponse>> downloadFile(String fileName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.getObject(request, AsyncResponseTransformer.toBytes()));
    }

    /**
     * 파일 삭제
     */
    @Override
    public Mono<ResultData<ResultCodes>> deleteFile(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return Mono.fromFuture(s3AsyncClient.deleteObject(request))
                .map(response -> {
                    log.info("삭제한 파일 명 : " + fileName);
                    return new ResultData<>(ResultCodes.SUCCESS, "파일 삭제에 성공했습니다.");
                })
                .onErrorMap(e -> {
                    log.error("파일 삭제 실패: {}", e.getMessage());
                    return new CustomException(ErrorCodes.FILE_DELETE_FAILED,
                            "파일 삭제에 실패하였습니다. (파일명 : " + fileName + ")");
                });
    }

    /**
     * 파일 조회
     */
    @Override
    public Mono<ResultData<List<String>>> listFiles(String prefix) {
        String adjustedPrefix = (prefix == null || prefix.isEmpty()) ? "" : prefix + "/";
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(adjustedPrefix)
                .build();

        return Mono.fromFuture(s3AsyncClient.listObjectsV2(request))
                .map(result -> {
                    List<String> keys = result.contents().stream()
                            .map(S3Object::key)
                            .filter(key -> !key.equals(adjustedPrefix))
                            .collect(Collectors.toList());
                    return new ResultData<>(keys, "파일 목록 조회가 완료되었습니다.");
                })
                .onErrorMap(e -> {
                    log.error("파일 목록 조회 실패: {}", e.getMessage());
                    return new RuntimeException("파일 목록 조회에 실패하였습니다. " + e.getMessage());
                });
    }
}