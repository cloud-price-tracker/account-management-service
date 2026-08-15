package cpt.api.account_management_service.model.response;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponseDetails(
        @Size(max = 64)
        String path,
        @Size(min = 3, max = 3)
        String status,
        @Size(min = 1, max = 64)
        String statusMessage,
        List<ErrorDetails> errors
) {
}
