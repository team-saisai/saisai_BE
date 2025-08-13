package com.saisai.domain.user.repository;

import com.saisai.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    Optional<User> findByProviderId(String providerId);
}
