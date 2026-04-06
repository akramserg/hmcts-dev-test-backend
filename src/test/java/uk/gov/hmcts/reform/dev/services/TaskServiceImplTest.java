package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceImplTest {

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl();
    }

    @Test
    void getAllTasks_returnsEmptyList() {
        List<TaskResponse> result = taskService.getAllTasks();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTaskById_returnsTaskWithMatchingId() {
        TaskResponse result = taskService.getTaskById(42L);
        assertNotNull(result);
        assertEquals(42L, result.id());
    }

    @Test
    void createTask_returnsTaskWithMatchingTitle() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Test task",
            "Some description",
            TaskStatus.OPEN,
            LocalDateTime.now().plusDays(1)
        );

        TaskResponse result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("Test task", result.title());
        assertEquals(TaskStatus.OPEN, result.status());
    }

    @Test
    void updateTaskStatus_returnsTaskWithUpdatedStatus() {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.CLOSED);

        TaskResponse result = taskService.updateTaskStatus(1L, request);

        assertNotNull(result);
        assertEquals(TaskStatus.CLOSED, result.status());
    }

    @Test
    void deleteTask_doesNotThrow() {
        assertDoesNotThrow(() -> taskService.deleteTask(1L));
    }
}
