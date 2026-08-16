package cpt.api.account_management_service.utils;

import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.model.request.UserDetails;
import cpt.api.account_management_service.model.request.UserLoginRequest;
import cpt.api.account_management_service.services.request_handling.UserLoginHandler;

public class UnitTestRequestUtils {
    public static AccountRegistrationRequest generateAccountRegistrationRequest() {
        return AccountRegistrationRequest.builder()
                .userDetails(UserDetails.builder()
                        .email("test@example.com")
                        .password("password")
                        .username("testUsername")
                        .build())
                .build();
    }

    public static AccountRegistrationRequest generateAccountRegistrationRequest(String user, String email, String password) {
        return AccountRegistrationRequest.builder()
                .userDetails(UserDetails.builder()
                        .email(email)
                        .password(password)
                        .username(user)
                        .build())
                .build();
    }

    public static UserLoginRequest generateLoginRequestDetails(String username, String password) {
        return UserLoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    public static UserLoginRequest generateLoginRequestDetails() {
        return UserLoginRequest.builder()
                .username("user1234")
                .password("password1234")
                .build();
    }
}
