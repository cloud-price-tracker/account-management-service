package cpt.api.account_management_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {
    private final Argon2PasswordEncoder encoder;
    private final Base64.Encoder urlSafeBase64Encoder;
    private final SecureRandom secureRandom;

    public CryptoService(
            @Value("${encryption.hashing.iterations:10}") int iterations,
            @Value("${encryption.hashing.memLimit:60000}") int memLimit,
            @Value("${encryption.hashing.hashLength:32}") int hashLength,
            @Value("${encryption.hashing.parallelism:1}") int parallelism,
            @Value("${encryption.hashing.saltLength:16}") int saltLength
    ) {
        this.encoder = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memLimit, iterations);
        this.urlSafeBase64Encoder = Base64.getUrlEncoder().withoutPadding();
        this.secureRandom = new SecureRandom();
    }

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
