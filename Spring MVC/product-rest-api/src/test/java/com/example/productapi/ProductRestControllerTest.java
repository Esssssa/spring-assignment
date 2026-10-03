package com.example.productapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Test
    void coversTheWholeApi() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();

        mvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("X-Api-Key header is required"));

        mvc.perform(get("/api/products").header("X-Api-Key", "k"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.timestamp").exists());

        mvc.perform(get("/api/products/1").header("X-Api-Key", "k"))
                .andExpect(jsonPath("$.data.name").value("Keyboard"));

        mvc.perform(get("/api/products/99").header("X-Api-Key", "k"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Product 99 was not found"));

        mvc.perform(post("/api/products").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Monitor\",\"price\":199.0}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.name").value("Monitor"));

        mvc.perform(post("/api/products").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.price").value("Price must be positive"))
                .andExpect(jsonPath("$.data").doesNotExist());

        mvc.perform(put("/api/products/2").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gaming Mouse\",\"price\":29.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Gaming Mouse"));

        mvc.perform(put("/api/products/99").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"price\":1.0}"))
                .andExpect(status().isNotFound());

        mvc.perform(delete("/api/products/2").header("X-Api-Key", "k")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/products/2").header("X-Api-Key", "k")).andExpect(status().isNotFound());

        mvc.perform(post("/api/products/quick").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"FromJson\",\"price\":5.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("FromJson"));

        mvc.perform(post("/api/products/quick").header("X-Api-Key", "k").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "FromForm").param("price", "6.5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("FromForm"));
    }
}
