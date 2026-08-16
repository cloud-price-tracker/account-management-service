package cpt.api.account_management_service.service.request_handling;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.exception.UserLoginException;
import cpt.api.account_management_service.model.entities.User;
import cpt.api.account_management_service.model.entities.UserValidation;
import cpt.api.account_management_service.model.request.UserLoginRequest;
import cpt.api.account_management_service.model.response.GeneralAuthenticationResponse;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.repository.UserValidationRepository;
import cpt.api.account_management_service.services.CryptoService;
import cpt.api.account_management_service.services.request_handling.UserLoginHandler;
import cpt.api.account_management_service.utils.UnitTestRequestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserLoginHandlerTest {

    @Mock
    private CryptoService cryptoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationRepository userValidationRepository;

    @InjectMocks
    private UserLoginHandler userLoginHandler;

    @Test
    public void handle_shouldReturnSuccessResponse_whenLoginInformationIsValidAndEmailConfirmed() {
        // Arrange
        String username = "user1234";
        String password = "password1234";
        String hashedPassword = "HashedPassword1234";

        User user = new User();
        user.setHashedPassword(hashedPassword);
        UserLoginRequest userLoginRequest = UnitTestRequestUtils.generateLoginRequestDetails(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cryptoService.verifyPassword(password, hashedPassword)).thenReturn(true);

        UserValidation userValidation = new UserValidation();
        userValidation.setEmailConfirmed(true);
        when(userValidationRepository.findByUser(user)).thenReturn(Optional.of(userValidation));

        // Act
        GeneralAuthenticationResponse generalAuthenticationResponse = userLoginHandler.handle(userLoginRequest);

        // Assert
        assertNotNull(generalAuthenticationResponse);
        assertEquals("jwtToken", generalAuthenticationResponse.accessToken());
        assertEquals("implement with API Gateway", generalAuthenticationResponse.refreshToken());

        verify(cryptoService, times(1)).verifyPassword(password, hashedPassword);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userValidationRepository, times(1)).findByUser(user);
    }

    @Test
    public void handle_shouldThrowUserLoginException_whenUserDoesNotExist() {
        // Arrange
        String username = "nonexistentUser";
        String password = "password1234";
        UserLoginRequest userLoginRequest = UnitTestRequestUtils.generateLoginRequestDetails(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UserLoginException exception = assertThrows(UserLoginException.class, () ->
                userLoginHandler.handle(userLoginRequest)
        );

        assertEquals(AccountManagementError.INVALID_LOGIN_INFORMATION, exception.getAccountManagementError());
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoInteractions(cryptoService, userValidationRepository);
    }

    @Test
    public void handle_shouldThrowUserLoginException_whenPasswordDoesNotMatch() {
        // Arrange
        String username = "user1234";
        String password = "wrongPassword";
        String hashedPassword = "HashedPassword1234";

        User user = new User();
        user.setHashedPassword(hashedPassword);
        UserLoginRequest userLoginRequest = UnitTestRequestUtils.generateLoginRequestDetails(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cryptoService.verifyPassword(password, hashedPassword)).thenReturn(false);

        // Act & Assert
        UserLoginException exception = assertThrows(UserLoginException.class, () ->
                userLoginHandler.handle(userLoginRequest)
        );

        assertEquals(AccountManagementError.INVALID_LOGIN_INFORMATION, exception.getAccountManagementError());
        verify(userRepository, times(1)).findByUsername(username);
        verify(cryptoService, times(1)).verifyPassword(password, hashedPassword);
        verifyNoInteractions(userValidationRepository);
    }

    @Test
    public void handle_shouldReturnLimitedToken_whenEmailIsUnconfirmed() {
        // Arrange
        String username = "user1234";
        String password = "password1234";
        String hashedPassword = "HashedPassword1234";

        User user = new User();
        user.setHashedPassword(hashedPassword);
        UserLoginRequest userLoginRequest = UnitTestRequestUtils.generateLoginRequestDetails(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cryptoService.verifyPassword(password, hashedPassword)).thenReturn(true);

        UserValidation userValidation = new UserValidation();
        userValidation.setEmailConfirmed(false);
        when(userValidationRepository.findByUser(user)).thenReturn(Optional.of(userValidation));

        // Act
        GeneralAuthenticationResponse generalAuthenticationResponse = userLoginHandler.handle(userLoginRequest);

        // Assert
        assertNotNull(generalAuthenticationResponse);
        assertEquals("limitedJwtToken", generalAuthenticationResponse.accessToken());
        assertEquals("implement with API Gateway", generalAuthenticationResponse.refreshToken());

        verify(cryptoService, times(1)).verifyPassword(password, hashedPassword);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userValidationRepository, times(1)).findByUser(user);
    }

    @Test
    public void handle_shouldReturnLimitedToken_whenUserValidationDoesNotExist() {
        // Arrange
        String username = "user1234";
        String password = "password1234";
        String hashedPassword = "HashedPassword1234";

        User user = new User();
        user.setHashedPassword(hashedPassword);
        UserLoginRequest userLoginRequest = UnitTestRequestUtils.generateLoginRequestDetails(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cryptoService.verifyPassword(password, hashedPassword)).thenReturn(true);

        when(userValidationRepository.findByUser(user)).thenReturn(Optional.empty());

        // Act
        GeneralAuthenticationResponse generalAuthenticationResponse = userLoginHandler.handle(userLoginRequest);

        // Assert
        assertNotNull(generalAuthenticationResponse);
        assertEquals("limitedJwtToken", generalAuthenticationResponse.accessToken());
        assertEquals("implement with API Gateway", generalAuthenticationResponse.refreshToken());

        verify(cryptoService, times(1)).verifyPassword(password, hashedPassword);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userValidationRepository, times(1)).findByUser(user);
    }
}
