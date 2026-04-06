package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

public record CreateTaskRequest(

    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Status is required")
    TaskStatus status,

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDateTime dueDate
) {}
