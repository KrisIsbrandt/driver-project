package pl.coderslab;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.coderslab.service.storage.AssetService;

@SpringBootApplication
public class DriverApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
