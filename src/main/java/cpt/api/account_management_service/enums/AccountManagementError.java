package cpt.api.account_management_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountManagementError {
    INVALID_HTTP_HEADER(HttpStatus.BAD_REQUEST, "Invalid HTTP header", "1000"),
    MISSING_HTTP_HEADER(HttpStatus.BAD_REQUEST, "Missing required HTTP header", "1001");

    private final HttpStatus status;
    private final String errorMessage;
    private final String errorCode;
}
