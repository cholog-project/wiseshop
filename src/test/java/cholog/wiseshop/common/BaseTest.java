package cholog.wiseshop.common;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import cholog.wiseshop.api.payment.controller.PaymentClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@ExtendWith(DatabaseClearExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseTest {

    @Autowired
    public MockMvc mockMvc;

    @MockitoBean
    protected PaymentClient paymentClient;
}
