package cholog.wiseshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WiseshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(WiseshopApplication.class, args);
    }

}
