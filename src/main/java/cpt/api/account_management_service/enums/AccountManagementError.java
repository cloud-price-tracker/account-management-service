package cpt.api.account_management_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountManagementError {
    INVALID_HTTP_HEADER_ERROR(HttpStatus.BAD_REQUEST, "Invalid HTTP header", "1000"),
    MISSING_HTTP_HEADER_ERROR(HttpStatus.BAD_REQUEST, "Missing required HTTP header", "1001"),

    INVALID_REQUEST_BODY_ERROR(HttpStatus.BAD_REQUEST, "Invalid or missing fields in request body", "1002"),
    NONUNIQUE_EMAIL_ERROR(HttpStatus.BAD_REQUEST, "Email address already in use", "1003"),
    NONUNIQUE_USERNAME_ERROR(HttpStatus.BAD_REQUEST, "Username already exists", "1004"),
    NONEXISTENT_EMAIL_ADDRESS_ERROR(HttpStatus.BAD_REQUEST, "Email address does not exist", "1005");

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorCode;
}
