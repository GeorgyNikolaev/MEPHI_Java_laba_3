package config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI missionAnalyzerOpenApi(
            @Value("${server.port:8080}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("Mission Analyzer API")
                        .version("1.0")
                        .description("REST API для загрузки миссий из файлов, хранения в PostgreSQL и генерации отчётов.")
                        .contact(new Contact().name("Mission Analyzer")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local")
                ));
    }
}
