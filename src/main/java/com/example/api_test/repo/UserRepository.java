package com.example.api_test.repo;


import com.example.api_test.entity.User_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User_info, Long> {
    Optional<User_info> findByUsername(String username);
    boolean existsByUsername(String username);
}