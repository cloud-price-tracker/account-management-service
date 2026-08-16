package cpt.api.account_management_service.model.response;

import lombok.Builder;

@Builder
public record GeneralAuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}
