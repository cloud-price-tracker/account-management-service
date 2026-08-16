package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.model.response.ErrorDetails;
import cpt.api.account_management_service.model.response.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static cpt.api.account_management_service.enums.AccountManagementError.MISSING_HTTP_HEADER_ERROR;
import static cpt.api.account_management_service.enums.AccountManagementError.NONUNIQUE_EMAIL_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    public void handleMissingRequestHeaderException_shouldReturnBadRequestAndErrorDetails_whenCalled() {
        // Arrange
        String uri = "/api/test-header";
        when(request.getRequestURI()).thenReturn(uri);

        InvalidHeaderException exception = new InvalidHeaderException("Missing or invalid headers in request");
        exception.getInvalidHeaderErrors().add(new CumulativeErrorWrapper(MISSING_HTTP_HEADER_ERROR, "Content-Type"));

        // Act
        ResponseEntity<GlobalErrorResponse> responseEntity = globalExceptionHandler.handleMissingRequestHeaderException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        GlobalErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("400", body.errorResponseDetails().status());
        assertEquals(uri, body.errorResponseDetails().path());
        
        List<ErrorDetails> errors = body.errorResponseDetails().errors();
        assertEquals(1, errors.size());
        assertEquals("1001", errors.getFirst().errorCode());
        assertTrue(errors.getFirst().errorMessage().contains(MISSING_HTTP_HEADER_ERROR.getErrorMessage()));
    }

    @Test
    public void handleInvalidRequestBodyException_shouldReturnBadRequestAndErrorDetails_whenCalled() {
        // Arrange
        String uri = "/api/test-body";
        when(request.getRequestURI()).thenReturn(uri);

        InvalidRequestBodyException exception = new InvalidRequestBodyException("One or more invalid fields found");
        exception.getInvalidBodyErrors().add(new CumulativeErrorWrapper(NONUNIQUE_EMAIL_ERROR, "email"));

        // Act
        ResponseEntity<GlobalErrorResponse> responseEntity = globalExceptionHandler.handleInvalidRequestBodyException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        GlobalErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("400", body.errorResponseDetails().status());
        assertEquals(uri, body.errorResponseDetails().path());

        List<ErrorDetails> errors = body.errorResponseDetails().errors();
        assertEquals(1, errors.size());
        assertEquals("1003", errors.getFirst().errorCode());
        assertTrue(errors.getFirst().errorMessage().contains(NONUNIQUE_EMAIL_ERROR.getErrorMessage()));
    }

    @Test
    public void handleValidationExceptions_shouldReturnBadRequestAndFieldErrors_whenCalled() {
        // Arrange
        String uri = "/api/test-validation";
        when(request.getRequestURI()).thenReturn(uri);

        FieldError fieldError = new FieldError("userDetails", "username", "must not be blank");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<GlobalErrorResponse> responseEntity = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        GlobalErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("400", body.errorResponseDetails().status());
        assertEquals(uri, body.errorResponseDetails().path());

        List<ErrorDetails> errors = body.errorResponseDetails().errors();
        assertEquals(1, errors.size());
        assertEquals("1002", errors.getFirst().errorCode());
        assertEquals("username must not be blank", errors.getFirst().errorMessage());
    }

    @Test
    public void handleNonCumulativeInvalidLoginException_shouldReturnStatusAndErrorDetails_whenCalled() {
        // Arrange
        String uri = "/api/test-non-cumulative";
        when(request.getRequestURI()).thenReturn(uri);

        NonCumulativeException exception = new NonCumulativeException(AccountManagementError.INVALID_LOGIN_INFORMATION);

        // Act
        ResponseEntity<GlobalErrorResponse> responseEntity = globalExceptionHandler.handleNonCumulativeException(exception, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        GlobalErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("401", body.errorResponseDetails().status());
        assertEquals(uri, body.errorResponseDetails().path());

        List<ErrorDetails> errors = body.errorResponseDetails().errors();
        assertEquals(1, errors.size());
        assertEquals(AccountManagementError.INVALID_LOGIN_INFORMATION.getErrorCode(), errors.getFirst().errorCode());
        assertEquals(AccountManagementError.INVALID_LOGIN_INFORMATION.getErrorMessage(), errors.getFirst().errorMessage());
    }
}
