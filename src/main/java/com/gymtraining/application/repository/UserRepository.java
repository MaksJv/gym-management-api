package com.gymtraining.application.repository;

import com.gymtraining.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByActiveTrue();

    Optional<User> findByUsernameAndActiveTrue(String username);

    boolean existsByUsername(String username);

}
