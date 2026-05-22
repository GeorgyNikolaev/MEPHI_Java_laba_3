package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "app",
        "api",
        "service",
        "persistence",
        "config"
})
@EntityScan(basePackages = "persistence.entity")
@EnableJpaRepositories(basePackages = "persistence.repository")
public class MissionWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MissionWebApplication.class, args);
    }
}
