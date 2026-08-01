package cpt.api.account_management_service.utils;

import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.model.request.UserDetails;

public class AccountRegistrationRequestUtils {
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
}
