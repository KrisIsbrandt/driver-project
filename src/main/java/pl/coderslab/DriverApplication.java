package pl.coderslab;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pl.coderslab.config.StorageProperties;
import pl.coderslab.service.storage.AssetService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //Create storage folder on app start
    @Bean
    CommandLineRunner init(AssetService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
