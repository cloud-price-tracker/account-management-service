package cpt.api.account_management_service.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record UserDetails(
        @Pattern(regexp = "^[^@]+@[^@]+\\.[^@]+$")
        @Size(min = 5, max = 64)
        @NotEmpty
        String email,

        @Pattern(regexp = "^{5,64}$")
        @Size(min = 5, max = 64)
        @NotEmpty
        String username,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]$")
        @Size(min = 10, max = 64)
        @NotEmpty
        String password
) {
}
