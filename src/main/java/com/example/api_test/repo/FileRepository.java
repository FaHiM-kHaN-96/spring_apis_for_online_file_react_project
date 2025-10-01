package com.example.api_test.repo;

import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File_Entity, Long> {
    List<File_Entity> findByUser(User_info user);

    @Transactional(readOnly = true)
    @Query("SELECT f FROM File_Entity f WHERE f.id = :fileId AND f.user.id = :userId")
    File_Entity findByIdAndUserId(@Param("fileId") Long fileId, @Param("userId") int userId);


    @Transactional
    @Modifying
    @Query("DELETE FROM File_Entity f WHERE f.id = :fileId AND f.user.id = :userId")
    int deleteByIdAndUserId(@Param("fileId") Long fileId, @Param("userId") int userId);




}