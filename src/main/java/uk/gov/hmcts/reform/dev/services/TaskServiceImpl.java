package uk.gov.hmcts.reform.dev.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    // TODO: remove once repository is implemented
    private TaskResponse stubTask(Long id) {
        return new TaskResponse(
            id,
            "Stub Title",
            "Stub Description",
            TaskStatus.OPEN,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return List.of();
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        return stubTask(id);
    }

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        return new TaskResponse(
            1L,
            request.title(),
            request.description(),
            request.status(),
            request.dueDate(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request) {
        TaskResponse stub = stubTask(id);
        return new TaskResponse(
            stub.id(),
            stub.title(),
            stub.description(),
            request.status(),
            stub.dueDate(),
            stub.createdAt(),
            LocalDateTime.now()
        );
    }

    @Override
    public void deleteTask(Long id) {
        // TODO: implement when repository is ready
    }
}
