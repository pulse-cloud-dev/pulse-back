package pulse.back.domain.api.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pulse.back.entity.mentoring.MentoringLocation;
import reactor.core.publisher.Mono;

@Service
public class GeocodingService {
    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private static final String GEOCODING_API_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

    @Autowired
    public GeocodingService(WebClient.Builder webClientBuilder,
                            @Value("${geocoding.naver.client-id}") String clientId,
                            @Value("${geocoding.naver.client-secret}") String clientSecret) {
        this.webClient = webClientBuilder.baseUrl(GEOCODING_API_URL).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }


    public Mono<MentoringLocation> getGeocodingResult(String address, String detailAddress) {
        String fullAddress = detailAddress != null && !detailAddress.isBlank()
                ? address + " " + detailAddress
                : address;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", fullAddress)
                        .build())
                .header("X-NCP-APIGW-API-KEY-ID", clientId)
                .header("X-NCP-APIGW-API-KEY", clientSecret)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGeocodingResponse)
                .onErrorResume(e -> {
                    System.err.println("지오코딩 API 호출 중 오류 발생: " + e.getMessage());
                    return Mono.empty();
                });
    }


    private MentoringLocation parseGeocodingResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("addresses") && rootNode.get("addresses").isArray() &&
                    rootNode.get("addresses").size() > 0) {

                JsonNode addressNode = rootNode.get("addresses").get(0);
                String x = addressNode.get("x").asText();
                String y = addressNode.get("y").asText();
                Double distance = addressNode.has("distance") ?
                        addressNode.get("distance").asDouble() : 0.0;

                return new MentoringLocation(x, y, distance);
            } else {
                System.err.println("지오코딩 결과가 없습니다: " + response);
                return null;
            }
        } catch (Exception e) {
            System.err.println("지오코딩 응답 파싱 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}