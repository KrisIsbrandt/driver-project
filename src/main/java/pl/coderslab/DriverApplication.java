package pl.coderslab;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pl.coderslab.config.StorageProperties;
import pl.coderslab.service.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
