package com.example.api_test.repo;



import com.example.api_test.entity.User_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User_info, Long> {
    Optional<User_info> findByUsername(String username);
    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User_info f SET f.verification_status = true WHERE f.id = :id")
    int updateOtpTrueByFileId(@Param("id") Integer id);


    @Transactional
    @Modifying
    @Query("UPDATE User_info f SET f.one_time_password = :one_time_password WHERE f.id = :id")
    int findbyotp(@Param("id") Integer id,@Param("one_time_password") String one_time_password );

    @Query("SELECT u.id FROM User_info u WHERE u.username = :username")
    Integer findIdByUser_Username(@Param("username") String username);

    @Query("SELECT u.username FROM User_info u WHERE u.id = :id")
    String findUsernameById(@Param("id") Integer id);

    @Query("SELECT u.username FROM User_info u WHERE u.one_time_password = :one_time_password")
    String findusernamebyotp(@Param("one_time_password") String one_time_password);

    @Query("SELECT u.verification_status FROM User_info u WHERE u.id = :id")
    boolean check_verification(@Param("id") Integer id);


    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END FROM User_info u WHERE u.one_time_password = :one_time_password")
    boolean isOtpAvailable(@Param("one_time_password") String one_time_password);

    @Transactional
    @Modifying
    @Query("UPDATE User_info f SET f.one_time_password = NULL WHERE f.id = :id")
    int deleteOtpByUserId_OTP(@Param("id") int id);



}