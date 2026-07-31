package cpt.api.account_management_service.services.validation;

public interface RequestValidator<T> {
    void validate(T request);
}
