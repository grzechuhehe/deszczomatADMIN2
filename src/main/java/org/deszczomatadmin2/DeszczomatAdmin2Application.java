package org.deszczomatadmin2;

import org.deszczomatadmin2.model.User;
import org.deszczomatadmin2.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class DeszczomatAdmin2Application {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication app = new SpringApplication(DeszczomatAdmin2Application.class);
        app.setHeadless(false); // bardzo ważne
        app.run(args);
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

    //TODO JAKIS LOG DLA JARA JAKO .TXT

    @Bean
    ApplicationRunner uiOnStart() {
        return args -> EventQueue.invokeLater(() -> {
            // małe potwierdzenie
            JOptionPane.showMessageDialog(null, "Aplikacja wystartowała.\nAdres: http://localhost:8080");
        });
    }
}
