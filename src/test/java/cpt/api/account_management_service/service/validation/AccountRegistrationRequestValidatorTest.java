package cpt.api.account_management_service.service.validation;

import cpt.api.account_management_service.exception.InvalidRequestBodyException;
import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.repository.UserRepository;
import cpt.api.account_management_service.services.validation.AccountRegistrationRequestValidator;
import cpt.api.account_management_service.utils.UnitTestRequestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountRegistrationRequestValidatorTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    AccountRegistrationRequestValidator accountRegistrationRequestValidator;

    @ParameterizedTest
    @MethodSource("verifyTestCases")
    public void verifyMethods_shouldReturnCorrectly(boolean isUnique) {
        // Arrange
        String testString = "testString";
        when(userRepository.existsByEmail(testString)).thenReturn(isUnique);
        when(userRepository.existsByUsername(testString)).thenReturn(isUnique);

        // Act
        boolean verifyEmailResult = accountRegistrationRequestValidator.verifyUniqueEmail(testString);
        boolean verifyUsernameResult = accountRegistrationRequestValidator.verifyUniqueUsername(testString);

        // Assert
        assertEquals(!verifyEmailResult, isUnique); // If email or username is unique, then method should return false
        assertEquals(!verifyUsernameResult, isUnique); // for uniqueness and vice versa
    }

    @ParameterizedTest
    @MethodSource("validateTestCases")
    public void validateMethod_shouldSuccessfullyValidate_withValidRequest(Boolean emailValid, Boolean emailExists, Boolean userExists, int errors) {
        // Arrange
        String email = "testEmail";
        String username = "testUsername";
        String password = "testPassword";
        AccountRegistrationRequest accountRegistrationRequest = UnitTestRequestUtils.generateAccountRegistrationRequest(
                username, email, password);

        when(userRepository.existsByEmail(email)).thenReturn(!emailExists);
        when(userRepository.existsByUsername(username)).thenReturn(!userExists);


        // Act & Assert
        if (emailValid && userExists && emailExists) {
            accountRegistrationRequestValidator.validate(accountRegistrationRequest);
        } else {
            InvalidRequestBodyException exception = assertThrows(InvalidRequestBodyException.class, () -> accountRegistrationRequestValidator.validate(accountRegistrationRequest));
            assertEquals(errors, exception.getInvalidBodyErrors().size());
        }

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).existsByUsername(username);
    }


    private static Stream<Arguments> verifyTestCases() {
        return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
        );
    }

    // Generate All Permutations of validation cases
    private static Stream<Arguments> validateTestCases() {
        Boolean[] booleanValues = new Boolean[]{true, false};

        Stream<Arguments> returnArgs = Stream.of();
        for (Boolean emailUnique : booleanValues) {
            for (Boolean userUnique : booleanValues) {
                // Keep emailExists hardcoded for now until a service to verify existence is implemented
                int errors = 0;
                if (!emailUnique) { errors++; }
                if (!userUnique) { errors++; }

                returnArgs = Stream.concat(returnArgs,
                        Stream.of(Arguments.of(true, emailUnique, userUnique, errors)));
            }
        }

        return returnArgs;
    }
}
