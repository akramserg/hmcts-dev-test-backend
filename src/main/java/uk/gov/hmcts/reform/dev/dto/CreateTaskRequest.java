package uk.gov.hmcts.reform.dev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

@Schema(description = "Request body for creating a task")
public record CreateTaskRequest(

    @Schema(description = "Title of the task", example = "Review pull request")
    @NotBlank(message = "Title is required")
    String title,

    @Schema(description = "Optional description", example = "Check the CI pipeline results")
    String description,

    @Schema(description = "Initial status of the task", example = "OPEN")
    @NotNull(message = "Status is required")
    TaskStatus status,

    @Schema(description = "Due date — must be in the future", example = "2026-12-01T09:00:00")
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDateTime dueDate
) {}
