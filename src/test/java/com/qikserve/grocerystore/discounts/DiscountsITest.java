package com.qikserve.grocerystore.discounts;

import basetest.IntegrationTest;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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

class DiscountsITest extends IntegrationTest {

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
