package hu.morkalla.instantpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableJpaRepositories
@SpringBootApplication
public class InstantPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstantPaymentApplication.class, args);
    }

}
