package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.TodoEntity;
import org.example.model.TodoRequest;
import org.example.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TodoService todoService;

    private TodoEntity expected;

    @BeforeEach
    void setUp() {
        this.expected = new TodoEntity();
        this.expected.setId(123L);
        this.expected.setOrder(0L);
        this.expected.setTitle("Test Title");
        this.expected.setCompleted(false);
    }

    @Test
    void create() throws Exception {
        when(this.todoService.add(any(TodoRequest.class)))
                .then((i) -> {
                    TodoRequest request = i.getArgument(0, TodoRequest.class);
                    return new TodoEntity(
                            this.expected.getId(),
                            request.getTitle(),
                            this.expected.getOrder(),
                            this.expected.getCompleted());
                });

        TodoRequest request = new TodoRequest();
        request.setTitle("Any Title");

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        this.mockMvc.perform(
                post("/").contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Any Title"));
    }

    @Test
    void readOne() throws Exception {
        when(this.todoService.searchById(123L))
                .thenReturn(this.expected);

        TodoRequest request = new TodoRequest();
        request.setTitle("Any Title");

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        this.mockMvc.perform(
                        get("/{id}", 123L).contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.order").value(0L))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void readAll() throws Exception {
        List<TodoEntity> expectedTodoes = new ArrayList<>();
        expectedTodoes.add(this.expected);

        when(this.todoService.searchAll())
                .thenReturn(expectedTodoes);

        TodoRequest request = new TodoRequest();
        request.setTitle("Any Title");

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        this.mockMvc.perform(
                get("/").contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].order").value(0L))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void updateOne() throws Exception {
        when(this.todoService.updateById(anyLong(), any(TodoRequest.class)))
                .then((i) -> {
                    TodoRequest request = i.getArgument(0, TodoRequest.class);
                    return new TodoEntity(
                            this.expected.getId(),
                            request.getTitle(),
                            this.expected.getOrder(),
                            this.expected.getCompleted());
                });

        TodoRequest request = new TodoRequest();
        request.setTitle("Any Title");
        request.setOrder(1L);
        request.setCompleted(false);

        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        this.mockMvc.perform(
                patch("/{id}", 123L).contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Any Title"));
    }

    @Test
    void deleteOne() {
    }

    @Test
    void deleteAll() {
    }
}