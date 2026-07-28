package cpt.api.account_management_service.repository;

import cpt.api.account_management_service.model.entities.UserValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserValidationRepository extends JpaRepository<UserValidation, UUID> {
}
