package cpt.api.account_management_service.exception;

import java.util.HashSet;
import java.util.Set;

public class CumulativeException extends RuntimeException {
    private final Set<CumulativeErrorWrapper> cumulativeErrors;

    public CumulativeException(String message) {
        super(message);
        this.cumulativeErrors = new HashSet<>();
    }

    protected Set<CumulativeErrorWrapper> getCumulativeErrors() {
        return this.cumulativeErrors;
    }
}
