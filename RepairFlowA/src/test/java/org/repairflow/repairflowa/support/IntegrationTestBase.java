package org.repairflow.repairflowa.support;

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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author guangyang
 * @date 3/8/26 16:43
 * @description TODO: Description
 */
@Testcontainers
@SpringBootTest(classes = RepairFlowAApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")

public abstract class IntegrationTestBase {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("repairflow_test")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TicketRepository ticketRepository;
//
//    @Autowired
//    protected DatabaseCleaner databaseCleaner;
//
//    @BeforeEach
//    void cleanDatabase() {
//        databaseCleaner.clean();
//    }

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        System.out.println("JDBC URL = " + postgres.getJdbcUrl());
        System.out.println("DB USER = " + postgres.getUsername());
        System.out.println("DB PASS = " + postgres.getPassword());

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

}
