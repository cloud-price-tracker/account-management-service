package cpt.api.account_management_service.model.response;

import lombok.Builder;

@Builder
public record GlobalErrorResponse(ErrorResponseDetails errorResponseDetails) {
}
