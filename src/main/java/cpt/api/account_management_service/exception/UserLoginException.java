package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;

public class UserLoginException extends NonCumulativeException {
    public UserLoginException(AccountManagementError error) {
        super(error);
    }
}
