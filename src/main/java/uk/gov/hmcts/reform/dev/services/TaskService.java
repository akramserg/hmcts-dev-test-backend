package uk.gov.hmcts.reform.dev.services;

import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import java.util.List;

public interface TaskService {
    List<TaskResponse> getAllTasks();
    TaskResponse getTaskById(Long id);
    TaskResponse createTask(CreateTaskRequest request);
    TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request);
    void deleteTask(Long id);
}