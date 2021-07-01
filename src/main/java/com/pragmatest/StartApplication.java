package com.pragmatest;

import com.pragmatest.models.UserEntity;
import com.pragmatest.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class StartApplication {
    public static void main(String[] args) {

        SpringApplication.run(StartApplication.class, args);
    }

    @Bean
    @Profile("demo")
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            userRepository.save(new UserEntity("John Smith", "London", 33));
            userRepository.save(new UserEntity("Mary Walsh", "Liverpool", 26));
            userRepository.save(new UserEntity("Harry Miles", "Bournemouth", 30));
        };
    }
}
