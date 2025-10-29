package co.id.abcd.secure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureApplication.class, args);
    }
}
