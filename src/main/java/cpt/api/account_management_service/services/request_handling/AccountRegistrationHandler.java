package cpt.api.account_management_service.services.request_handling;

import cpt.api.account_management_service.model.entities.User;
import cpt.api.account_management_service.model.entities.UserValidation;
import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.model.response.AccountRegistrationResponse;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.repository.UserValidationRepository;
import cpt.api.account_management_service.services.CryptoSerivce;
import cpt.api.account_management_service.services.TimeService;
import cpt.api.account_management_service.services.validation.AccountRegistrationRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountRegistrationHandler implements RequestHandler<AccountRegistrationRequest, AccountRegistrationResponse> {

    private final AccountRegistrationRequestValidator requestValidator;
    private final CryptoSerivce cryptoSerivce;
    private final TimeService timeService;

    private final UserRepository userRepository;
    private final UserValidationRepository userValidationRepository;

    @Override
    @Transactional
    public AccountRegistrationResponse handle(AccountRegistrationRequest request) {
        requestValidator.validate(request);

        User user = createUser(request);

        User savedUser = userRepository.save(user);
        UserValidation userValidation = createUserValidation(savedUser);
        UserValidation savedUserValidation = userValidationRepository.save(userValidation);
        log.info("Successfully registered user: {} with ID: {} and userValidation {}", savedUser.getUsername(), savedUser.getId(), savedUserValidation.getId());

        return new AccountRegistrationResponse("jwtToken", "implement with API Gateway");
    }

    public User createUser(AccountRegistrationRequest request) {
        String hashedPassword = cryptoSerivce.hashPassword(request.userDetails().password());

        User user = new User();
        user.setHashedPassword(hashedPassword);
        user.setDateCreated(Instant.now());
        user.setUsername(request.userDetails().username());
        user.setEmail(request.userDetails().email());

        return user;
    }

    public UserValidation createUserValidation(User savedUser) {
        UserValidation userValidation = new UserValidation();
        userValidation.setUser(savedUser);
        userValidation.setEmailToken(cryptoSerivce.generateEmailToken());
        userValidation.setEmailTokenExpiry(timeService.getFutureInstant(300));

        return userValidation;
    }
}
