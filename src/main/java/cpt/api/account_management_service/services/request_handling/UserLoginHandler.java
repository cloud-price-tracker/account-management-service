package cpt.api.account_management_service.services.request_handling;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.exception.UserLoginException;
import cpt.api.account_management_service.model.entities.User;
import cpt.api.account_management_service.model.entities.UserValidation;
import cpt.api.account_management_service.model.request.UserLoginRequest;
import cpt.api.account_management_service.model.response.GeneralAuthenticationResponse;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.repository.UserValidationRepository;
import cpt.api.account_management_service.services.CryptoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserLoginHandler implements RequestHandler<UserLoginRequest, GeneralAuthenticationResponse> {

    private final CryptoService cryptoService;
    private final UserRepository userRepository;
    private final UserValidationRepository userValidationRepository;

    @Override
    public GeneralAuthenticationResponse handle(UserLoginRequest request) {
        log.debug("Handling user login request");

        Optional<User> fetchedUser = userRepository.findByUsername(request.username());
        if (fetchedUser.isEmpty() || !cryptoService.verifyPassword(request.password(), fetchedUser.get().getHashedPassword())) {
            throw new UserLoginException(AccountManagementError.INVALID_LOGIN_INFORMATION);
        }

        if (!checkEmailConfirmed(fetchedUser.get())) {
            log.info("User email is unconfirmed, preparing to send remediation confirmation email.");
            // TODO - Handle email unconfirmed flow by sending email to user.
            // TODO - Also provide users with a limited JWT, so that they can validate email but no more!
            return new GeneralAuthenticationResponse("limitedJwtToken", "implement with API Gateway");
        }

        return new GeneralAuthenticationResponse("jwtToken", "implement with API Gateway");
    }

    private boolean checkEmailConfirmed(User fetchedUser) {
        Optional<UserValidation> userValidation = userValidationRepository.findByUser(fetchedUser);

        return userValidation.isPresent() && userValidation.get().isEmailConfirmed();
    }
}
