package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/tasks";
        taskRepository.deleteAll();
    }

    @Test
    void createTask_returns201AndPersists() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Integration task", "Some description", TaskStatus.OPEN,
            LocalDateTime.now().plusDays(3)
        );

        ResponseEntity<TaskResponse> response = restTemplate.postForEntity(baseUrl, request, TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Integration task");
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void getAllTasks_returnsListOfTasks() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Task A", null, TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );
        restTemplate.postForEntity(baseUrl, request, TaskResponse.class);

        ResponseEntity<TaskResponse[]> response = restTemplate.getForEntity(baseUrl, TaskResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].title()).isEqualTo("Task A");
    }

    @Test
    void getTaskById_returnsTask() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Findable task", null, TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );
        TaskResponse created = restTemplate.postForEntity(baseUrl, request, TaskResponse.class).getBody();

        ResponseEntity<TaskResponse> response = restTemplate.getForEntity(
            baseUrl + "/" + created.id(), TaskResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Findable task");
    }

    @Test
    void getTaskById_returns404_whenNotFound() {
        ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/9999", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateTaskStatus_returns200WithNewStatus() {
        CreateTaskRequest createRequest = new CreateTaskRequest(
            "Status task", null, TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );
        TaskResponse created = restTemplate.postForEntity(baseUrl, createRequest, TaskResponse.class).getBody();

        UpdateTaskStatusRequest updateRequest = new UpdateTaskStatusRequest(TaskStatus.CLOSED);
        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            baseUrl + "/" + created.id(), HttpMethod.PATCH,
            new HttpEntity<>(updateRequest), TaskResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.CLOSED);
    }

    @Test
    void deleteTask_returns204AndRemovesTask() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Deletable task", null, TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );
        TaskResponse created = restTemplate.postForEntity(baseUrl, request, TaskResponse.class).getBody();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/" + created.id(), HttpMethod.DELETE,
            HttpEntity.EMPTY, Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl + "/" + created.id(), Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
