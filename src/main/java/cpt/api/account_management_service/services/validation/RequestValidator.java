package cpt.api.account_management_service.services.validation;

// Simple interface to force standardization of request validation. Any validations beyond simple field validation
// should be implemented in a child class that implements this interface. Simple validation can be implemented through
// Jarkarta and @Value validation annotations.
public interface RequestValidator<T> {
    void validate(T request);
}
