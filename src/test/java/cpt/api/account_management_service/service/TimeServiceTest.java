package cpt.api.account_management_service.service;

import cpt.api.account_management_service.services.TimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TimeServiceTest {
    @InjectMocks
    private TimeService timeService;

    @Test
    public void timeServiceGetFutureInstant_shouldReturnCorrectFutureTime_whenCalled() {
        // Arrange
        Instant expectedFutureTime = Instant.now().plusSeconds(10);
        // Act
        Instant actualFutureTime = timeService.getFutureInstant(10);
        // Assert
        assertEquals(expectedFutureTime.getEpochSecond(), actualFutureTime.getEpochSecond());
    }

    @ParameterizedTest
    @MethodSource("isInstantExpiredTestCases")
    public void timeServiceIsExpired_shouldCorrectlyMarkExpired_whenInstantIsExpired(int seconds, boolean isExpired) {
        // Arrange
        Instant expiryInstant = Instant.now();
        Instant testInstant = expiryInstant.plusSeconds(seconds);
        // Act
        boolean result = timeService.isInstantExpired(testInstant, expiryInstant);
        // Assert
        assertEquals(isExpired, result);
    }

    private static Stream<Arguments> isInstantExpiredTestCases() {
        return Stream.of(
                Arguments.of(10, true),
                Arguments.of(-10, false)
        );
    }
}
