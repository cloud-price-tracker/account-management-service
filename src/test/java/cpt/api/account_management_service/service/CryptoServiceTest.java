package cpt.api.account_management_service.service;

import cpt.api.account_management_service.services.CryptoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static cpt.api.account_management_service.constants.RegexExpressions.URL_SAFE_CHAR_REGEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CryptoServiceTest {

    private final CryptoService cryptoService = new CryptoService(10, 60000, 32, 1, 16);

    @Test
    public void cryptoServiceHashPassword_shouldReturnHashedPassword_whenCalled() {
        // Arrange
        String rawPassword = "securePassword123";

        // Act
        String hashedPassword = cryptoService.hashPassword(rawPassword);

        // Assert
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$argon2"));
    }

    @ParameterizedTest
    @MethodSource("passwordVerificationTestCases")
    public void cryptoServiceVerifyPassword_shouldVerifyCorrectly_whenTested(String rawPassword, String testPassword, boolean expectedResult) {
        // Arrange
        String hashedPassword = cryptoService.hashPassword(rawPassword);

        // Act
        boolean result = cryptoService.verifyPassword(testPassword, hashedPassword);

        // Assert
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> passwordVerificationTestCases() {
        return Stream.of(
                Arguments.of("securePassword123", "securePassword123", true),
                Arguments.of("securePassword123", "wrongPassword", false),
                Arguments.of("securePassword123", "", false)
        );
    }

    @Test
    public void cryptoServiceGenerateEmailToken_shouldReturnBase64UrlSafeToken_whenCalled() {
        // Act
        String token = cryptoService.generateEmailToken();

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.matches(URL_SAFE_CHAR_REGEX));
    }

    @Test
    public void cryptoServiceVerifyPassword_shouldCorrectlyVerifyPassword () {
        // Arrange
        String testPassword = "testPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = cryptoService.hashPassword(testPassword);

        // Act
        boolean correctValidation = cryptoService.verifyPassword(testPassword, hashedPassword);
        boolean incorrectValidation = cryptoService.verifyPassword(wrongPassword, hashedPassword);

        // Assert
        assertTrue(correctValidation);
        assertFalse(incorrectValidation);
    }
}
