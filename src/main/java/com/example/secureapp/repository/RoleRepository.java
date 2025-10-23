package com.example.secureapp.repository;

import com.example.secureapp.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    Boolean existsByName(String name);

    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Role> searchRoles(String keyword, Pageable pageable);
}
