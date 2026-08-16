package cpt.api.account_management_service.controllers;

import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import cpt.api.account_management_service.model.response.GeneralAuthenticationResponse;
import cpt.api.account_management_service.services.request_handling.AccountRegistrationHandler;
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
public class AccountManagementController {

    private final AccountRegistrationHandler requestHandler;

    @PostMapping("${app.route.registration}")
    public ResponseEntity<GeneralAuthenticationResponse> registerAccount(@RequestBody @Valid AccountRegistrationRequest accountRegistrationRequest) {
        GeneralAuthenticationResponse responseObj = requestHandler.handle(accountRegistrationRequest);

        return ResponseEntity.ok().body(responseObj);
    }
}
