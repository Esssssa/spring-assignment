package com.example.tasktracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {"tasktracker.max-tasks=2", "tasktracker.default-page-size=1"})
class TaskControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Test
    void walksThroughTheApi() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
        String body = "{\"title\":\"Write report\",\"dueDate\":\"2026-12-01\"}";

        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"));
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\" \"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/tasks")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/tasks?limit=5")).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/tasks?completed=true&limit=5")).andExpect(jsonPath("$.length()").value(0));

        mvc.perform(patch("/api/tasks/1/complete")).andExpect(status().isOk());
        mvc.perform(get("/api/tasks?completed=true")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/tasks/99")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tasks/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/tasks/1")).andExpect(status().isNotFound());
    }
}
