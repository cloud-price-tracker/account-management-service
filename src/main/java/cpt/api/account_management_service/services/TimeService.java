package cpt.api.account_management_service.services;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeService {

    public boolean isInstantExpired(Instant instant, Instant expiration) {
        return instant.isAfter(expiration);
    }

    public Instant getFutureInstant(long duration) {
        return Instant.now().plusSeconds(duration);
    }
}
