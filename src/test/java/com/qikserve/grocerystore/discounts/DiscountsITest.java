package com.qikserve.grocerystore.discounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForEmptyJson;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(name = "discounts-service", port = 8081)
})
@SpringBootTest
class DiscountsITest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectWireMock("discounts-service")
    private WireMockServer wireMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDiscount() throws Exception {
        Discount discount = Discount.builder()
                .id("1")
                .staticAmount(Integer.MAX_VALUE)
                .description("<description>")
                .build();

        wireMock.stubFor(WireMock.get("/discounts/1")
                .willReturn(okForJson(discount)));

        String discountAsString = objectMapper.writeValueAsString(discount);

        mockMvc.perform(get("/discounts/1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(discountAsString)
                );
    }

    @Test
    void postDiscount() throws Exception {
        Discount created = Discount.builder()
                .id("1")
                .staticAmount(Integer.MAX_VALUE)
                .description("<description>")
                .build();

        DiscountRequest discountRequest = DiscountRequest.builder()
                .staticAmount(Integer.MAX_VALUE)
                .description("<description>")
                .build();
        String requestAsString = objectMapper.writeValueAsString(discountRequest);

        wireMock.stubFor(WireMock.post("/discounts")
                .withRequestBody(equalToJson(requestAsString))
                .willReturn(okForJson(created))
        );

        mockMvc.perform(
                post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(Charset.defaultCharset())
                        .content(requestAsString)
        ).andExpectAll(
                status().isCreated(),
                header().exists("location"),
                header().string("location", "1")
        );
    }

    @Test
    void putDiscount() throws Exception {
        Discount existing = Discount.builder()
                .id("1")
                .description("<description")
                .staticAmount(Integer.MAX_VALUE)
                .build();

        wireMock.stubFor(WireMock.get("/discounts/1")
                .willReturn(okForJson(existing)));

        DiscountRequest discountRequest = DiscountRequest.builder()
                .description("<another-description>")
                .staticAmount(Integer.MIN_VALUE)
                .build();

        String requestAsString = objectMapper.writeValueAsString(discountRequest);

        wireMock.stubFor(WireMock.put("/discounts/1")
                .withRequestBody(equalToJson(requestAsString))
                .willReturn(okForEmptyJson()));

        mockMvc.perform(
                        put("/discounts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(Charset.defaultCharset())
                                .content(requestAsString)
                )
                .andExpect(status().isOk());
    }
}
