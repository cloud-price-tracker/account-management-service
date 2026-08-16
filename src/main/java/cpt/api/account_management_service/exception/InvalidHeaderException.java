package cpt.api.account_management_service.exception;

import java.util.Set;

public class InvalidHeaderException extends CumulativeException {

    public InvalidHeaderException(String message) {
        super(message);
    }

    public Set<CumulativeErrorWrapper> getInvalidHeaderErrors() {
        return this.getCumulativeErrors();
    }
}
