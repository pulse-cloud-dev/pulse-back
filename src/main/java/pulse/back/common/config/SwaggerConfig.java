package pulse.back.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Security 스키마 이름 정의
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT") // JWT 형식 지정
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 인증 적용
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("PULSE API docs")
                .description("PULSE API 서비스 docs<br/>" +
                        "<br/>" +
                        "----------------------------<br/>" +
                        "api의 request 파라미터에 대한 개별적인 description은 해당 페이지 최하단의 \"Schemas\"를 참고하시면 됩니다.<br/>" +
                        "별표 (<font color=\"red\">*</font>) : 필수값. <br/>" +
                        "[...] 을 누르시면 해당 값의 자세한 설명을 확인 하실 수 있습니다.<br/>" +
                        "----------------------------<br/>")
                .version("1.0.0");
    }
}
