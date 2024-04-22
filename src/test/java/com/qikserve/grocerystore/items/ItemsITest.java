package com.qikserve.grocerystore.items;

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

class ItemsITest extends IntegrationTest {

    @Test
    void getItem() throws Exception {
        Item item = Item.builder()
                .id("1")
                .name("<name>")
                .price(Integer.MAX_VALUE)
                .description("<description>")
                .build();

        wireMock.stubFor(WireMock.get("/items/1")
                .willReturn(okForJson(item)));

        String discountAsString = objectMapper.writeValueAsString(item);

        mockMvc.perform(get("/items/1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(discountAsString)
                );
    }

    @Test
    void postItem() throws Exception {
        Item created = Item.builder()
                .id("1")
                .name("<name>")
                .price(Integer.MAX_VALUE)
                .description("<description>")
                .build();

        ItemRequest discountRequest = ItemRequest.builder()
                .name("<name>")
                .price(Integer.MAX_VALUE)
                .description("<description>")
                .build();
        String requestAsString = objectMapper.writeValueAsString(discountRequest);

        wireMock.stubFor(WireMock.post("/items")
                .withRequestBody(equalToJson(requestAsString))
                .willReturn(okForJson(created))
        );

        mockMvc.perform(
                post("/items")
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
    void putItem() throws Exception {
        Item existing = Item.builder()
                .id("1")
                .name("<name>")
                .price(Integer.MAX_VALUE)
                .description("<description>")
                .build();

        wireMock.stubFor(WireMock.get("/items/1")
                .willReturn(okForJson(existing)));

        ItemRequest discountRequest = ItemRequest.builder()
                .name("<name>")
                .price(Integer.MAX_VALUE)
                .description("<another-description>")
                .build();

        String requestAsString = objectMapper.writeValueAsString(discountRequest);

        wireMock.stubFor(WireMock.put("/items/1")
                .withRequestBody(equalToJson(requestAsString))
                .willReturn(okForEmptyJson()));

        mockMvc.perform(
                        put("/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(Charset.defaultCharset())
                                .content(requestAsString)
                )
                .andExpect(status().isOk());
    }
}
