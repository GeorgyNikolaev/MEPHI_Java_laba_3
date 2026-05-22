package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import parser.MissionParserFactory;
import reporter.ReportFactory;

@Configuration
public class DomainBeansConfig {

    @Bean
    public MissionParserFactory missionParserFactory() {
        return new MissionParserFactory();
    }

    @Bean
    public ReportFactory reportFactory() {
        return new ReportFactory();
    }
}
