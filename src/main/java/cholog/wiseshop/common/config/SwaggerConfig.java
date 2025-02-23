package cholog.wiseshop.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server()
                    .url("https://api.wiseshop.kro.kr")
                    .description("Production Server"),
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development")
            ))
            .info(new Info()
                .title("wiseshop API")
                .version("v1.0")
                .description("wiseshop API")
            );
    }
}
