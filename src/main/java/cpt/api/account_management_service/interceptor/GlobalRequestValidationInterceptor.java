package cpt.api.account_management_service.interceptor;

import cpt.api.account_management_service.enums.AccountManagementError;
import cpt.api.account_management_service.exception.CumulativeErrorWrapper;
import cpt.api.account_management_service.exception.InvalidHeaderException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

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

        if (!invalidHeaderException.getInvalidHeaderErrors().isEmpty()) {
            log.error("Missing required header in request", invalidHeaderException);
            throw invalidHeaderException;
        }
        return true;
    }

    private CumulativeErrorWrapper validateHeaderValue(HttpServletRequest request, String requestHeader, String expectedValue) {
        CumulativeErrorWrapper missingHeaderException = validateHeaderPresence(request, requestHeader);
        if (missingHeaderException != null) { return missingHeaderException; }

        String headerValue = request.getHeader(requestHeader);
        if (!headerValue.equals(expectedValue)) {
            log.debug("Invalid header value: {}, expected: {}, in request", requestHeader, expectedValue);
            return new CumulativeErrorWrapper(AccountManagementError.INVALID_HTTP_HEADER_ERROR, requestHeader);
        }
        return null;
    }

    private CumulativeErrorWrapper validateHeaderPresence(HttpServletRequest request, String requestHeader) {
        String headerValue = request.getHeader(requestHeader);
        if (headerValue == null) {
            log.debug("Missing required header: {}, in request", requestHeader);
            return new CumulativeErrorWrapper(AccountManagementError.MISSING_HTTP_HEADER_ERROR, requestHeader);
        }
        return null;
    }

    private InvalidHeaderException validateRequiredHeaders(HttpServletRequest request) {
        InvalidHeaderException invalidHeaderException = new InvalidHeaderException("Missing or invalid headers in request");

        if (DispatcherType.REQUEST.equals(request.getDispatcherType())) {
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderValue(request, CONTENT_TYPE, APPLICATION_JSON_VALUE));
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderValue(request, ACCEPT, APPLICATION_JSON_VALUE));
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderValue(request, CACHE_CONTROL, CACHE_CONTROL_NO_STORE));

            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderPresence(request, REQUEST_ID_HEADER));
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderPresence(request, CORRELATION_ID_HEADER));
        } else if (DispatcherType.FORWARD.equals(request.getDispatcherType())) {
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderPresence(request, REQUEST_ID_HEADER));
            addIfNotNull(invalidHeaderException.getInvalidHeaderErrors(), validateHeaderPresence(request, CORRELATION_ID_HEADER));
        }

        return invalidHeaderException;
    }

    private void addIfNotNull(Set<CumulativeErrorWrapper> list, CumulativeErrorWrapper error) {
        if (error != null) {
            list.add(error);
        }
    }
}
