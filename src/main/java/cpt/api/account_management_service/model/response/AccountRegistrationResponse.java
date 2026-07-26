package cpt.api.account_management_service.model.response;

import lombok.Builder;

@Builder
public record AccountRegistrationResponse(
        String accessToken,
        String refreshToken,
        ErrorResponseDetails errorResponseDetails
) {
}
