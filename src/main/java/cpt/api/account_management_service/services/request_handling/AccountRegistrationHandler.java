package cpt.api.account_management_service.services.request_handling;

import cpt.api.account_management_service.model.entities.User;
import cpt.api.account_management_service.model.entities.UserValidation;
import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.model.response.GeneralAuthenticationResponse;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.repository.UserValidationRepository;
import cpt.api.account_management_service.services.CryptoService;
import cpt.api.account_management_service.services.TimeService;
import cpt.api.account_management_service.services.validation.AccountRegistrationRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class AccountRegistrationHandler implements RequestHandler<AccountRegistrationRequest, GeneralAuthenticationResponse> {

    private final AccountRegistrationRequestValidator requestValidator;
    private final CryptoService cryptoService;
    private final TimeService timeService;

    private final UserRepository userRepository;
    private final UserValidationRepository userValidationRepository;

    private final Long emailTokenExpiryDuration;

    public AccountRegistrationHandler(@Value("${encryption.email-token.duration:300}") long emailTokenExpiryDuration,
                                      CryptoService cryptoService, TimeService timeService, UserRepository userRepository,
                                      UserValidationRepository userValidationRepository, AccountRegistrationRequestValidator validator) {
        this.emailTokenExpiryDuration = emailTokenExpiryDuration == 0 ? 300 : emailTokenExpiryDuration;
        this.cryptoService = cryptoService;
        this.timeService = timeService;
        this.userRepository = userRepository;
        this.userValidationRepository = userValidationRepository;
        this.requestValidator = validator;
    }

    @Override
    @Transactional /* TODO - REMOVE COMMENT LATER - Transactional annotation is used to ensure that the entire operation is atomic
    i.e. it is entirely completed or not at all which is required for data consistency of the user and userValidation database objects */
    public GeneralAuthenticationResponse handle(AccountRegistrationRequest request) {
        log.debug("Handling user registration request");
        requestValidator.validate(request);

        User user = createUser(request);
        User savedUser = userRepository.save(user);

        UserValidation userValidation = createUserValidation(savedUser);
        UserValidation savedUserValidation = userValidationRepository.save(userValidation);
        log.info("Successfully registered user: {} with ID: {} and userValidation {}", savedUser.getUsername(), savedUser.getId(), savedUserValidation.getId());

        return new GeneralAuthenticationResponse("jwtToken", "implement with API Gateway");
    }

    public User createUser(AccountRegistrationRequest request) {
        String hashedPassword = cryptoService.hashPassword(request.userDetails().password());

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
        userValidation.setEmailToken(cryptoService.generateEmailToken());
        userValidation.setEmailTokenExpiry(timeService.getFutureInstant(emailTokenExpiryDuration));

        return userValidation;
    }
}
