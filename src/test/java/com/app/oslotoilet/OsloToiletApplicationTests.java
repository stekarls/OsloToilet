package com.app.oslotoilet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "jwt.secret=test-only-secret-key-that-is-at-least-32-bytes")
@Import(TestContainerConfig.class)
class OsloToiletApplicationTests {

    @Test
    void contextLoads() {
    }

}
