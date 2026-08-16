package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;
import lombok.Getter;

@Getter
public class NonCumulativeException extends RuntimeException {
    private final AccountManagementError accountManagementError;

    public NonCumulativeException(AccountManagementError error) {
        accountManagementError = error;
        super(error.getErrorMessage());
    }
}
