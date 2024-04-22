package com.qikserve.grocerystore.carts;

import basetest.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.items.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForEmptyJson;
import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.util.Collections.emptyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartsITest extends IntegrationTest {

    @Test
    void getCart() throws Exception {
        wireMock.stubFor(WireMock.get("/carts/1").willReturn(
                okForJson(CartModel.builder().id("1").items(List.of("1")).discounts(List.of("1")).total(50).build())
        ));

        wireMock.stubFor(WireMock.get("/items/1").willReturn(
                okForJson(Item.builder().id("1").price(100).build())
        ));

        wireMock.stubFor(WireMock.get("/discounts/1").willReturn(
                okForJson(Discount.builder().id("1").staticAmount(50).build())
        ));

        String expected = objectMapper.writeValueAsString(
                Cart.builder()
                        .id("1")
                        .items(List.of(Item.builder().id("1").price(100).build()))
                        .discounts(List.of(Discount.builder().id("1").staticAmount(50).build()))
                        .total(50)
                        .build()
        );

        mockMvc.perform(get("/carts/1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(expected)
                );
    }

    @Test
    void createCart() throws Exception {
        wireMock.stubFor(WireMock.get("/items/1").willReturn(
                okForJson(Item.builder().id("1").price(100).build())
        ));

        wireMock.stubFor(WireMock.get("/discounts/1").willReturn(
                okForJson(Discount.builder().id("1").staticAmount(50).build())
        ));

        wireMock.stubFor(WireMock.post("/carts").willReturn(
                okForJson(CartModel.builder().id("1").items(List.of("1")).discounts(List.of("1")).total(50).build())
        ));


        String requestAsString = objectMapper.writeValueAsString(
                CartRequest.builder()
                        .itemIds(List.of("1"))
                        .discountIds(List.of("1"))
                        .build()
        );

        mockMvc.perform(
                        post("/carts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(Charset.defaultCharset())
                                .content(requestAsString)
                )
                .andExpectAll(
                        status().isCreated(),
                        header().exists("location"),
                        header().string("location", "1")
                );
    }

    @Test
    void putCart() throws Exception {
        wireMock.stubFor(WireMock.get("/carts/1")
                .willReturn(
                        okForJson(CartModel.builder().id("1").items(List.of("1")).discounts(List.of("1")).total(50).build())
                ));

        wireMock.stubFor(WireMock.get("/items/1").willReturn(
                okForJson(Item.builder().id("1").price(100).build())
        ));

        wireMock.stubFor(WireMock.get("/discounts/1").willReturn(
                okForJson(Discount.builder().id("1").staticAmount(50).build())
        ));

        wireMock.stubFor(
                WireMock.put("/carts/1")
                        .inScenario(STARTED)
                        .willSetStateTo("UPDATED")
                        .willReturn(okForEmptyJson())
        );

        wireMock.stubFor(WireMock.get("/carts/1")
                .inScenario(STARTED)
                .whenScenarioStateIs("UPDATED")
                .willReturn(
                        okForJson(CartModel.builder().id("1").items(List.of("1")).discounts(emptyList()).total(100).build())
                ));

        String requestAsString = objectMapper.writeValueAsString(
                CartRequest.builder()
                        .itemIds(List.of("1"))
                        .discountIds(emptyList())
                        .build()
        );

        String expected = objectMapper.writeValueAsString(
                Cart.builder()
                        .id("1")
                        .items(List.of(Item.builder().id("1").price(100).build()))
                        .discounts(emptyList())
                        .total(100)
                        .build()
        );

        mockMvc.perform(
                        put("/carts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(Charset.defaultCharset())
                                .content(requestAsString)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json(expected)
                );
    }
}
