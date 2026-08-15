package cpt.api.account_management_service.model.response;

import lombok.Builder;

@Builder
public record ErrorDetails(
        String errorCode,
        String errorMessage
) {
}
