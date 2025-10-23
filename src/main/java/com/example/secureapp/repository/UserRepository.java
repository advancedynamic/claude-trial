package com.example.secureapp.repository;

import com.example.secureapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);

    Optional<User> findByMagicLinkToken(String token);

    @Query("SELECT u FROM User u WHERE u.passwordResetToken = ?1 AND u.passwordResetTokenExpiry > ?2")
    Optional<User> findByValidPasswordResetToken(String token, LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.magicLinkToken = ?1 AND u.magicLinkTokenExpiry > ?2")
    Optional<User> findByValidMagicLinkToken(String token, LocalDateTime now);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<User> searchUsers(String keyword, Pageable pageable);
}
