package cpt.api.account_management_service.exception;

import cpt.api.account_management_service.model.response.AccountRegistrationResponse;
import cpt.api.account_management_service.model.response.ErrorDetails;
import cpt.api.account_management_service.model.response.ErrorResponseDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidHeaderException.class)
    public ResponseEntity<AccountRegistrationResponse> handleMissingRequestHeaderException(InvalidHeaderException exception) {
        List<ErrorDetails> errors = new ArrayList<>();
        for (InvalidHeaderException ex : exception.getInvalidHeaders()) {
            errors.add(ErrorDetails.builder()
                    .errorCode(ex.error.getErrorCode())
                    .errorMessage(ex.error.getErrorMessage() + ". Header: '" + ex.getHeaderName() + "'.")
                    .build());
        }

        ErrorResponseDetails errorResponseDetails = ErrorResponseDetails.builder()
                .path(exception.getUri())
                .status(String.valueOf(BAD_REQUEST.value()))
                .statusMessage(BAD_REQUEST.getReasonPhrase())
                .errors(errors)
                .build();


        AccountRegistrationResponse response = AccountRegistrationResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .errorResponseDetails(errorResponseDetails)
                .build();

        return new ResponseEntity<>(response, BAD_REQUEST);
    }
}
