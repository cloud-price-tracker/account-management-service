package cpt.api.account_management_service.controllers;

import cpt.api.account_management_service.model.request.UserLoginRequest;
import cpt.api.account_management_service.model.response.GeneralAuthenticationResponse;
import cpt.api.account_management_service.services.request_handling.UserLoginHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.route.prefix}")
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final UserLoginHandler userLoginHandler;

    @PostMapping("${app.route.login}")
    public ResponseEntity<GeneralAuthenticationResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        GeneralAuthenticationResponse response = userLoginHandler.handle(userLoginRequest);

        return ResponseEntity.ok(response);
    }
}

/* Game Plan:
Part 1
1 - need to create request and response objects
2 - need to create validators for the objects, likely only annotation level
3 - create controller + controller methods

Part 2
4 - create login service + methods
4.1 - check against database and use hashed password method to check login is good
4.2 - set up logic for logging flows (email unvalidated or validated), but without the email send.

Part 3
5 - unit tests

 */