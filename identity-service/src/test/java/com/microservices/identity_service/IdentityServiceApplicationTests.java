package com.microservices.identity_service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class IdentityServiceApplicationTests {

	@Test
	void contextLoads() {
        log.info("Test case execute");
        assertTrue(true);
	}

}
