package basetest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(name = "external-service-store", port = 8081)
})
@SpringBootTest
public abstract class IntegrationTest {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @InjectWireMock("external-service-store")
    protected WireMockServer wireMock;

    @Autowired
    protected MockMvc mockMvc;
}
