package org.deszczomatadmin2;

import org.deszczomatadmin2.model.User;
import org.deszczomatadmin2.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DeszczomatAdmin2Application {

    public static void main(String[] args) {
        SpringApplication.run(DeszczomatAdmin2Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").orElseGet(() -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setRole("ROLE_ADMIN");
                return userRepository.save(admin);
            });
        };
    }
}
