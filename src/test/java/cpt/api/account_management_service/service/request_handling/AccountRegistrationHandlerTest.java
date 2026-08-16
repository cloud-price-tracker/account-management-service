package cpt.api.account_management_service.service.request_handling;

import cpt.api.account_management_service.model.entities.User;
import cpt.api.account_management_service.model.entities.UserValidation;
import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.repository.UserValidationRepository;
import cpt.api.account_management_service.services.CryptoService;
import cpt.api.account_management_service.services.TimeService;
import cpt.api.account_management_service.services.request_handling.AccountRegistrationHandler;
import cpt.api.account_management_service.services.validation.AccountRegistrationRequestValidator;
import cpt.api.account_management_service.utils.UnitTestRequestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountRegistrationHandlerTest {
    private static final long emailTokenExpiration= 300;

    @Mock
    private AccountRegistrationRequestValidator requestValidator;
    
    @Mock
    private CryptoService cryptoService;
    
    @Mock
    private TimeService timeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationRepository userValidationRepository;

    private AccountRegistrationHandler accountRegistrationHandler;

    @BeforeEach // Required because there exists a primitive (long) in AccountRegistrationHandler, so the
    // @InjectMocks annotation struggles to correctly instantiate the class
    void setUp() {
        accountRegistrationHandler = new AccountRegistrationHandler(
                emailTokenExpiration,
                cryptoService,
                timeService,
                userRepository,
                userValidationRepository,
                requestValidator
        );
    }

    @Test
    void handle_shouldSuccessfullyCreateUserAndValidation_whenCalled() {
        // Arrange
        AccountRegistrationRequest request = UnitTestRequestUtils.generateAccountRegistrationRequest();
        String hashedPassword = "hashedPassword";
        String emailToken = "emailToken";
        Instant futureInstant = Instant.now();
        User returnUser = new User();
        UserValidation savedUserValidation = new UserValidation();

        doNothing().when(requestValidator).validate(request);

        when(cryptoService.hashPassword(request.userDetails().password())).thenReturn(hashedPassword);
        when(userRepository.save(any())).thenReturn(returnUser);

        when(cryptoService.generateEmailToken()).thenReturn("emailToken");
        when(timeService.getFutureInstant(emailTokenExpiration)).thenReturn(futureInstant);
        when(userValidationRepository.save(any())).thenReturn(savedUserValidation);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<UserValidation> userValidationCaptor = ArgumentCaptor.forClass(UserValidation.class);

        // Act
        accountRegistrationHandler.handle(request);

        // Assert
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userValidationRepository, times(1)).save(userValidationCaptor.capture());
        verify(cryptoService, times(1)).generateEmailToken();
        verify(cryptoService, times(1)).hashPassword(request.userDetails().password());
        verify(timeService, times(1)).getFutureInstant(emailTokenExpiration);

        assertEquals(hashedPassword, userCaptor.getValue().getHashedPassword());

        assertEquals(emailToken, userValidationCaptor.getValue().getEmailToken());
        assertEquals(futureInstant, userValidationCaptor.getValue().getEmailTokenExpiry());
    }

    @Test
    public void createUser_shouldReturnNewUserBasedOnRequest_whenCalled() {
        // Arrange
        String user = "testUser";
        String email = "test@example.com";
        String password = "password";
        String hashedPassword = "hashedPassword";
        AccountRegistrationRequest request = UnitTestRequestUtils.generateAccountRegistrationRequest(user, email, password);

        when(cryptoService.hashPassword(password)).thenReturn("hashedPassword");

        // Act
        User returnUser = accountRegistrationHandler.createUser(request);

        // Assert
        assertEquals(user, returnUser.getUsername());
        assertEquals(email, returnUser.getEmail());
        assertEquals(hashedPassword, returnUser.getHashedPassword());
    }

    @Test
    public void createUserValidation_shouldReturnNewUserValidationWithUser_whenCalled() {
        // Arrange
        User user = new User();
        String emailToken = "emailToken";
        Instant emailTokenInstant = Instant.now();

        when(cryptoService.generateEmailToken()).thenReturn(emailToken);
        when(timeService.getFutureInstant(emailTokenExpiration)).thenReturn(emailTokenInstant);

        // Act
        UserValidation returnUserValidation = accountRegistrationHandler.createUserValidation(user);

        // Assert
        assertEquals(user, returnUserValidation.getUser());
        assertEquals(emailToken, returnUserValidation.getEmailToken());
        assertEquals(emailTokenInstant, returnUserValidation.getEmailTokenExpiry());
    }
}
