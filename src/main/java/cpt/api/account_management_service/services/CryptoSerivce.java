package cpt.api.account_management_service.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoSerivce {
    private final static int iterations = 10;
    private final static int memLimit = 60000;
    private final static int hashLength = 32;
    private final static int parallelism = 1;
    private final static int saltLength = 16;

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memLimit, iterations);
    private final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder urlSafeBase64Encoder = Base64.getUrlEncoder().withoutPadding();


    public String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }

    public String generateEmailToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return urlSafeBase64Encoder.encodeToString(randomBytes);
    }
}
