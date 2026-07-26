package cpt.api.account_management_service.controllers;

import cpt.api.account_management_service.model.request.AccountRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api") // TODO parameterize this with environment variables
@Slf4j
public class AccountManagementController {

    // TODO RETURN RESPONSE WRAPPER INSTEAD OF OBJECT
    @PostMapping("/authentication/register")
    public ResponseEntity<Object> registerAccount(@RequestBody AccountRegistrationRequest accountRegistrationRequest) {
        return ResponseEntity.ok().body(accountRegistrationRequest);
    }

    @GetMapping("/hello/world")
    public String helloWorld() {
        return "Hello World!\n";
    }
}
