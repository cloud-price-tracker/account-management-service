package cpt.api.account_management_service.exception;

import java.util.Set;

public class InvalidRequestBodyException extends CumulativeException {
    public InvalidRequestBodyException(String message) {
        super(message);
    }

    public Set<CumulativeErrorWrapper> getInvalidBodyErrors() {
        return this.getCumulativeErrors();
    }
}
