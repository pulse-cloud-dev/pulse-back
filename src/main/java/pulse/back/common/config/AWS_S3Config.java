package pulse.back.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import pulse.back.common.GlobalVariables;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@DependsOn("globalVariables")
public class AWS_S3Config {

    @Value("${aws.s3.region-name}")
    private String regionName;

    //비동기때 사용 예정
    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                GlobalVariables.AMAZON.ACCESS_KEY, GlobalVariables.AMAZON.SECRET_KEY
        );

        return S3AsyncClient.builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                GlobalVariables.AMAZON.ACCESS_KEY, GlobalVariables.AMAZON.SECRET_KEY
        );

        return S3Client.builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
