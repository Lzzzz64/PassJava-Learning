package org.example.passjava.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.jwt.secret=U8DDPRiGakUDEwVRjxxRkMP1c1RfYj0W8SmE+InP83c="
})
class PassjavaGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
