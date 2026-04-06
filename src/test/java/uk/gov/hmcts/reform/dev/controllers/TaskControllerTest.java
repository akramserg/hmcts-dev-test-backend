package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.exceptions.GlobalExceptionHandler;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TaskResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(taskService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        sampleResponse = new TaskResponse(
            1L, "Test task", "Description", TaskStatus.OPEN,
            LocalDateTime.now().plusDays(1), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void createTask_returns201WithBody() throws Exception {
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(sampleResponse);

        CreateTaskRequest request = new CreateTaskRequest(
            "Test task", "Description", TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test task"));
    }

    @Test
    void getAllTasks_returns200WithList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Test task"));
    }

    @Test
    void getTaskById_returns200WithTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTaskById_returns404_whenNotFound() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/tasks/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskStatus_returns200WithUpdatedTask() throws Exception {
        when(taskService.updateTaskStatus(eq(1L), any(UpdateTaskStatusRequest.class)))
            .thenReturn(sampleResponse);

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.CLOSED);

        mockMvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void updateTaskStatus_returns404_whenNotFound() throws Exception {
        when(taskService.updateTaskStatus(eq(99L), any(UpdateTaskStatusRequest.class)))
            .thenThrow(new TaskNotFoundException(99L));

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.CLOSED);

        mockMvc.perform(patch("/tasks/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_returns404_whenNotFound() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/tasks/99"))
            .andExpect(status().isNotFound());
    }
}
