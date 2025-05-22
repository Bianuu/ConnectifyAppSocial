package com.example.disiprojectbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DisiProjectBackendApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void modelMapperBeanExists() {
        DisiProjectBackendApplication app = new DisiProjectBackendApplication();
        assertNotNull(app.modelMapper(), "ModelMapper bean should not be null");
    }
}
