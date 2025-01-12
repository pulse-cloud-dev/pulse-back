package pulse.back.domain.s3.service;


import org.springframework.http.codec.multipart.FilePart;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

public interface FileService {
    Mono<ResultData<ResultCodes>> uploadFile(FilePart file, String path);
    Mono<ResponseBytes<GetObjectResponse>> downloadFile(String fileName);
    Mono<ResultData<ResultCodes>> deleteFile(String fileName);
    Mono<ResultData<List<String>>> listFiles(String fileName);
}