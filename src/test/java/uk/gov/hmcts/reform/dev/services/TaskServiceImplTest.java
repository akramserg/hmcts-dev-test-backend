package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
        sampleTask = Task.builder()
            .title("Test task")
            .description("Test description")
            .status(TaskStatus.OPEN)
            .dueDate(LocalDateTime.now().plusDays(1))
            .build();
    }

    @Test
    void getAllTasks_returnsEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskResponse> result = taskService.getAllTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository).findAll();
    }

    @Test
    void getAllTasks_returnsMappedTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test task", result.get(0).title());
    }

    @Test
    void getTaskById_returnsTask_whenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("Test task", result.title());
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_throwsTaskNotFoundException_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
        verify(taskRepository).findById(99L);
    }

    @Test
    void createTask_savesAndReturnsTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        CreateTaskRequest request = new CreateTaskRequest(
            "Test task", "Test description", TaskStatus.OPEN, LocalDateTime.now().plusDays(1)
        );

        TaskResponse result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("Test task", result.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_updatesAndReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.CLOSED);
        TaskResponse result = taskService.updateTaskStatus(1L, request);

        assertNotNull(result);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void updateTaskStatus_throwsTaskNotFoundException_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
            () -> taskService.updateTaskStatus(99L, new UpdateTaskStatusRequest(TaskStatus.CLOSED)));
    }

    @Test
    void deleteTask_deletesSuccessfully_whenFound() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_throwsTaskNotFoundException_whenNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(any());
    }
}
