package cpt.api.account_management_service.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static cpt.api.account_management_service.constants.RegexExpressions.EMAIL_REGEX;
import static cpt.api.account_management_service.constants.RegexExpressions.PASSWORD_REGEX;
import static cpt.api.account_management_service.constants.RegexExpressions.USERNAME_REGEX;

@Builder
public record UserDetails(
        @Pattern(regexp = EMAIL_REGEX)
        @Size(min = 5, max = 64)
        @NotEmpty
        String email,

        @Pattern(regexp = USERNAME_REGEX)
        @Size(min = 5, max = 64)
        @NotEmpty
        String username,

        @Pattern(regexp = PASSWORD_REGEX)
        @Size(min = 10, max = 64)
        @NotEmpty
        String password
) {
}
