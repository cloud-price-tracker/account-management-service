package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class CumulativeErrorWrapper {
    private AccountManagementError error;
    private String fieldName;

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, error);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof CumulativeErrorWrapper other)) { return false; }

        return Objects.equals(this.fieldName, other.fieldName)
                && Objects.equals(this.error, other.error);
    }
}
