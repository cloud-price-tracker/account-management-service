package cpt.api.account_management_service.model.request;

import lombok.Builder;

@Builder // TODO add some sort of error handling for invalid input so we can control the error flow
public record AccountRegistrationRequest(UserDetails userDetails) {}
