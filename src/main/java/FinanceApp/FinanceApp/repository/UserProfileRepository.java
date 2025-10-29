package FinanceApp.FinanceApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import FinanceApp.FinanceApp.domain.UserProfile;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
