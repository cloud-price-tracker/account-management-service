package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.model.response.ErrorDetails;
import cpt.api.account_management_service.model.response.ErrorResponseDetails;
import cpt.api.account_management_service.model.response.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static cpt.api.account_management_service.enums.AccountManagementError.INVALID_REQUEST_BODY_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidHeaderException.class)
    public ResponseEntity<GlobalErrorResponse> handleMissingRequestHeaderException(InvalidHeaderException exception,
                                                                                   HttpServletRequest request) {
        HttpStatus associatedStatus = BAD_REQUEST;

        List<ErrorDetails> errors = new ArrayList<>();
        for (CumulativeErrorWrapper error : exception.getInvalidHeaderErrors()) {
            errors.add(ErrorDetails.builder()
                    .errorCode(error.getError().getErrorCode())
                    .errorMessage(error.getError().getErrorMessage() + ". Header: '" + error.getFieldName() + "'.")
                    .build());
        }

        GlobalErrorResponse response = produceErrorResponse(associatedStatus, errors, request.getRequestURI());

        return new ResponseEntity<>(response, associatedStatus);
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidRequestBodyException(InvalidRequestBodyException exception,
                                                                                   HttpServletRequest request) {
        HttpStatus associatedStatus = BAD_REQUEST;

        List<ErrorDetails> errors = new ArrayList<>();
        for (CumulativeErrorWrapper error : exception.getInvalidBodyErrors()) {
            errors.add(ErrorDetails.builder()
                    .errorCode(error.getError().getErrorCode())
                    .errorMessage(error.getError().getErrorMessage())
                    .build());
        }

        GlobalErrorResponse response = produceErrorResponse(associatedStatus, errors, request.getRequestURI());

        return new ResponseEntity<>(response, associatedStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        AccountManagementError associatedError = INVALID_REQUEST_BODY_ERROR;

        List<ErrorDetails> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorDetails.builder()
                        .errorCode(associatedError.getErrorCode())
                        .errorMessage(fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .build())
                .toList();

        GlobalErrorResponse response = produceErrorResponse(associatedError.getStatus(), errors, request.getRequestURI());

        return new ResponseEntity<>(response, associatedError.getStatus());
    }

    @ExceptionHandler(NonCumulativeException.class)
    public ResponseEntity<GlobalErrorResponse> handleNonCumulativeException(NonCumulativeException exception, HttpServletRequest request) {
        AccountManagementError associatedError = exception.getAccountManagementError();
        List<ErrorDetails> errorList = List.of(new ErrorDetails(associatedError.getErrorCode(), associatedError.getErrorMessage()));

        GlobalErrorResponse errorResponse = produceErrorResponse(associatedError.getStatus(), errorList, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, associatedError.getStatus());
    }

    public GlobalErrorResponse produceErrorResponse(HttpStatus status, List<ErrorDetails> errors, String uri) {
        ErrorResponseDetails errorResponseDetails = ErrorResponseDetails.builder()
                .path(uri)
                .status(String.valueOf(status.value()))
                .statusMessage(status.getReasonPhrase())
                .errors(errors)
                .build();

        return GlobalErrorResponse.builder()
                .errorResponseDetails(errorResponseDetails).build();
    }
}
