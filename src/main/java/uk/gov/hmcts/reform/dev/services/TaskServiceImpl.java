package uk.gov.hmcts.reform.dev.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(TaskResponse::from)
            .toList();
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        return taskRepository.findById(id)
            .map(TaskResponse::from)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = Task.builder()
            .title(request.title())
            .description(request.description())
            .status(request.status())
            .dueDate(request.dueDate())
            .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(request.status());
        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
