package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class InvalidHeaderException extends RuntimeException {
    private final Set<InvalidHeaderException> invalidHeaders;
    AccountManagementError error;
    String headerName;
    String uri;

    public InvalidHeaderException(AccountManagementError error, String headerName, String uri) {
        super(error.getErrorMessage());
        this.invalidHeaders = new HashSet<>();
        this.error = error;
        this.headerName = headerName;
        this.uri = uri;
    }

    public InvalidHeaderException(String uri) {
        this.invalidHeaders = new HashSet<>();
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerName, error);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof InvalidHeaderException other)) { return false; }

        return Objects.equals(this.headerName, other.headerName)
                && Objects.equals(this.error, other.error);
    }
}
