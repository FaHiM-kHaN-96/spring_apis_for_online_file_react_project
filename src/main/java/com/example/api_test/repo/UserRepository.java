package com.example.api_test.repo;


import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User_info, Long> {
    Optional<User_info> findByUsername(String username);
    boolean existsByUsername(String username);


    @Query("SELECT u.id FROM User_info u WHERE u.username = :username")
    Integer findIdByUser_Username(@Param("username") String username);
}