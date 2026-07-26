package cpt.api.account_management_service.interceptor;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.exception.InvalidHeaderException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import static cpt.api.account_management_service.constants.AccountManagementConstants.CACHE_CONTROL_NO_STORE;
import static cpt.api.account_management_service.constants.AccountManagementConstants.CORRELATION_ID_HEADER;
import static cpt.api.account_management_service.constants.AccountManagementConstants.REQUEST_ID_HEADER;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class GlobalRequestValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        InvalidHeaderException invalidHeaderException = validateRequiredHeaders(request);

        if (!invalidHeaderException.getInvalidHeaders().isEmpty()) {
            throw invalidHeaderException;
        }
        return true;
    }

    private InvalidHeaderException validateHeaderValue(HttpServletRequest request, String requestHeader, String expectedValue) {
        InvalidHeaderException missingHeaderException = validateHeaderPresence(request, requestHeader);
        if (missingHeaderException != null) { return missingHeaderException; }

        String headerValue = request.getHeader(requestHeader);
        if (!headerValue.equals(expectedValue)) {
            log.error("Invalid header value: {}, expected: {}, in request", requestHeader, expectedValue);
            return new InvalidHeaderException(AccountManagementError.INVALID_HTTP_HEADER, requestHeader, request.getRequestURI());
        }
        return null;
    }

    private InvalidHeaderException validateHeaderPresence(HttpServletRequest request, String requestHeader) {
        String headerValue = request.getHeader(requestHeader);
        if (headerValue == null) {
            log.error("Missing required header: {}, in request", requestHeader);
            return new InvalidHeaderException(AccountManagementError.MISSING_HTTP_HEADER,
                    requestHeader, request.getRequestURI());
        }
        return null;
    }

    private InvalidHeaderException validateRequiredHeaders(HttpServletRequest request) {
        InvalidHeaderException invalidHeaderException = new InvalidHeaderException(request.getRequestURI());
        invalidHeaderException.getInvalidHeaders().add(null);

        if (DispatcherType.REQUEST.equals(request.getDispatcherType())) {
            invalidHeaderException.getInvalidHeaders().add(validateHeaderValue(request, CONTENT_TYPE, APPLICATION_JSON_VALUE));
            invalidHeaderException.getInvalidHeaders().add(validateHeaderValue(request, ACCEPT, APPLICATION_JSON_VALUE));
            invalidHeaderException.getInvalidHeaders().add(validateHeaderValue(request, CACHE_CONTROL, CACHE_CONTROL_NO_STORE));

            invalidHeaderException.getInvalidHeaders().add(validateHeaderPresence(request, REQUEST_ID_HEADER));
            invalidHeaderException.getInvalidHeaders().add(validateHeaderPresence(request, CORRELATION_ID_HEADER));
        } else if (DispatcherType.FORWARD.equals(request.getDispatcherType())) {
            invalidHeaderException.getInvalidHeaders().add(validateHeaderPresence(request, REQUEST_ID_HEADER));
            invalidHeaderException.getInvalidHeaders().add(validateHeaderPresence(request, CORRELATION_ID_HEADER));
        }

        invalidHeaderException.getInvalidHeaders().remove(null);
        return invalidHeaderException;
    }
}
