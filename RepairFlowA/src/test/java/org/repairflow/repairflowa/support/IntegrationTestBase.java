package org.repairflow.repairflowa.support;

import org.junit.jupiter.api.AfterEach;
import org.repairflow.repairflowa.RepairFlowAApplication;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author guangyang
 * @date 3/8/26 16:43
 * @description TODO: Description
 */

@SpringBootTest(classes = RepairFlowAApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")

public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("repairflow_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgres.start();
        System.out.println("TESTCONTAINER STARTED: " + postgres.getJdbcUrl());
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TicketRepository ticketRepository;

    @AfterEach
    void cleanUpBase() {
        ticketRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {


        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

}
