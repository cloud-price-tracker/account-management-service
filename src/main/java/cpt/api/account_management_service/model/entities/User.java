package cpt.api.account_management_service.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 64, unique = true)
    private String email;

    // Argon2 hashes can vary, so 128 is a safe ceiling
    @Column(name = "hashed_password", nullable = false, length = 128)
    private String hashedPassword;

    @Column(name = "username", nullable = false, length = 64, unique = true)
    private String username;

    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;
}
