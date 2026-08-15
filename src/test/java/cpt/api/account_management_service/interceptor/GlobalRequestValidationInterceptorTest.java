package cpt.api.account_management_service.interceptor;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.exception.CumulativeErrorWrapper;
import cpt.api.account_management_service.exception.InvalidHeaderException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static cpt.api.account_management_service.constants.AccountManagementConstants.CORRELATION_ID_HEADER;
import static cpt.api.account_management_service.constants.AccountManagementConstants.REQUEST_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalRequestValidationInterceptorTest {

    @InjectMocks
    private GlobalRequestValidationInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @Test
    public void preHandle_shouldReturnTrue_whenAllRequiredHeadersArePresentAndValid_forRequestDispatcher() {
        // Arrange
        when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(request.getHeader(HttpHeaders.CONTENT_TYPE)).thenReturn("application/json");
        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn("application/json");
        when(request.getHeader(HttpHeaders.CACHE_CONTROL)).thenReturn("no-store");
        when(request.getHeader(REQUEST_ID_HEADER)).thenReturn("req-123");
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("corr-123");

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
    }

    @Test
    public void preHandle_shouldThrowInvalidHeaderException_whenContentTypeHeaderIsMissing_forRequestDispatcher() {
        // Arrange
        when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(request.getHeader(HttpHeaders.CONTENT_TYPE)).thenReturn(null);
        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn("application/json");
        when(request.getHeader(HttpHeaders.CACHE_CONTROL)).thenReturn("no-store");
        when(request.getHeader(REQUEST_ID_HEADER)).thenReturn("req-123");
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("corr-123");

        // Act & Assert
        InvalidHeaderException exception = assertThrows(InvalidHeaderException.class, () -> 
            interceptor.preHandle(request, response, handler)
        );

        assertEquals(1, exception.getInvalidHeaderErrors().size());
        CumulativeErrorWrapper error = exception.getInvalidHeaderErrors().iterator().next();
        assertEquals("Content-Type", error.getFieldName());
        assertEquals(AccountManagementError.MISSING_HTTP_HEADER_ERROR, error.getError());
    }

    @Test
    public void preHandle_shouldThrowInvalidHeaderException_whenContentTypeHeaderIsInvalid_forRequestDispatcher() {
        // Arrange
        when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(request.getHeader(HttpHeaders.CONTENT_TYPE)).thenReturn("text/plain");
        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn("application/json");
        when(request.getHeader(HttpHeaders.CACHE_CONTROL)).thenReturn("no-store");
        when(request.getHeader(REQUEST_ID_HEADER)).thenReturn("req-123");
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("corr-123");

        // Act & Assert
        InvalidHeaderException exception = assertThrows(InvalidHeaderException.class, () -> 
            interceptor.preHandle(request, response, handler)
        );

        assertEquals(1, exception.getInvalidHeaderErrors().size());
        CumulativeErrorWrapper error = exception.getInvalidHeaderErrors().iterator().next();
        assertEquals("Content-Type", error.getFieldName());
        assertEquals(AccountManagementError.INVALID_HTTP_HEADER_ERROR, error.getError());
    }

    @Test
    public void preHandle_shouldReturnTrue_whenOnlyRequestIdAndCorrelationIdArePresent_forForwardDispatcher() {
        // Arrange
        when(request.getDispatcherType()).thenReturn(DispatcherType.FORWARD);
        when(request.getHeader(REQUEST_ID_HEADER)).thenReturn("req-123");
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("corr-123");

        // Act
        boolean result = interceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
    }

    @Test
    public void preHandle_shouldThrowInvalidHeaderException_whenRequestIdHeaderIsMissing_forForwardDispatcher() {
        // Arrange
        when(request.getDispatcherType()).thenReturn(DispatcherType.FORWARD);
        when(request.getHeader(REQUEST_ID_HEADER)).thenReturn(null);
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("corr-123");

        // Act & Assert
        InvalidHeaderException exception = assertThrows(InvalidHeaderException.class, () -> 
            interceptor.preHandle(request, response, handler)
        );

        assertEquals(1, exception.getInvalidHeaderErrors().size());
        CumulativeErrorWrapper error = exception.getInvalidHeaderErrors().iterator().next();
        assertEquals(REQUEST_ID_HEADER, error.getFieldName());
        assertEquals(AccountManagementError.MISSING_HTTP_HEADER_ERROR, error.getError());
    }
}
