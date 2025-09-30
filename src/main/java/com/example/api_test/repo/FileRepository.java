package com.example.api_test.repo;

import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FileRepository extends JpaRepository<File_Entity, Long> {
    List<File_Entity> findByUser(User_info user);
}