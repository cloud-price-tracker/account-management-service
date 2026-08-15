package cpt.api.account_management_service.services.validation;

import cpt.api.account_management_service.exception.CumulativeErrorWrapper;
import cpt.api.account_management_service.exception.InvalidRequestBodyException;
import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static cpt.api.account_management_service.enums.AccountManagementError.NONEXISTENT_EMAIL_ADDRESS_ERROR;
import static cpt.api.account_management_service.enums.AccountManagementError.NONUNIQUE_EMAIL_ERROR;
import static cpt.api.account_management_service.enums.AccountManagementError.NONUNIQUE_USERNAME_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRegistrationRequestValidator implements RequestValidator<AccountRegistrationRequest> {
    private final UserRepository userRepository;

    @Override
    public void validate(AccountRegistrationRequest request) {
        CompletableFuture<Boolean> emailExistsFuture = CompletableFuture.supplyAsync(() -> verifyEmailExists(request.userDetails().email()));
        CompletableFuture<Boolean> emailUniqueFuture = CompletableFuture.supplyAsync(() -> verifyUniqueEmail(request.userDetails().email()));
        CompletableFuture<Boolean> usernameUniqueFuture = CompletableFuture.supplyAsync(() -> verifyUniqueUsername(request.userDetails().username()));

        CompletableFuture.allOf(emailExistsFuture, emailUniqueFuture, usernameUniqueFuture).join();

        boolean emailExists = emailExistsFuture.join();
        boolean uniqueEmail = emailUniqueFuture.join();
        boolean uniqueUsername = usernameUniqueFuture.join();

        InvalidRequestBodyException invalidRequestBodyException = new InvalidRequestBodyException("One or more invalid fields found in " + request.getClass());
        if (!emailExists) {
            log.debug("Email is unreachable or does not exist: {}", request.userDetails().email());
            invalidRequestBodyException.getInvalidBodyErrors().add(new CumulativeErrorWrapper(NONEXISTENT_EMAIL_ADDRESS_ERROR, "email"));
        }

        if (!uniqueEmail) {
            log.debug("Non-unique email: {}", request.userDetails().email());
            invalidRequestBodyException.getInvalidBodyErrors().add(new CumulativeErrorWrapper(NONUNIQUE_EMAIL_ERROR, "email"));
        }

        if (!uniqueUsername) {
            log.debug("Non-unique username: {}", request.userDetails().username());
            invalidRequestBodyException.getInvalidBodyErrors().add(new CumulativeErrorWrapper(NONUNIQUE_USERNAME_ERROR, "username"));
        }

        if (!invalidRequestBodyException.getInvalidBodyErrors().isEmpty()) {
            log.error("Invalid request body: {} for {}", request.userDetails(), request.getClass(), invalidRequestBodyException);
            throw invalidRequestBodyException;
        }
    }

    public boolean verifyEmailExists(String email) {
        return true; // TODO implement service to validate existence of email
    }

    public boolean verifyUniqueEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean verifyUniqueUsername(String username) {
        return !userRepository.existsByUsername(username);
    }
}
