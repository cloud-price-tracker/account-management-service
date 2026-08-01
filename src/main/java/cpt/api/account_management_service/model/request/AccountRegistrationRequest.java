package cpt.api.account_management_service.model.request;

import lombok.Builder;

@Builder
public record AccountRegistrationRequest(UserDetails userDetails) {}
