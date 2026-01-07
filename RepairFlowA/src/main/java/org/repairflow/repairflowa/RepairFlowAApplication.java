package org.repairflow.repairflowa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RepairFlowAApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepairFlowAApplication.class, args);
    }

}
