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


@Repository
public interface FileRepository extends JpaRepository<File_Entity, Long> {
    List<File_Entity> findByUser(User_info user);

    @Transactional(readOnly = true)
    @Query("SELECT f FROM File_Entity f WHERE f.id = :fileId AND f.user.id = :userId")
    File_Entity findByIdAndUserId(@Param("fileId") Long fileId, @Param("userId") int userId);


    @Transactional(readOnly = true)
    @Query("SELECT f FROM File_Entity f WHERE f.fileotp = :fileotp")
    File_Entity findByFileOtp(@Param("fileotp") String fileotp);


    @Transactional
    @Modifying
    @Query("UPDATE File_Entity f SET f.fileotp = :fileotp WHERE f.id = :fileId AND f.user.id = :userId")
    int updateOtpByFileIdAndUserId(@Param("fileotp") String fileotp,
                                   @Param("fileId") Long fileId,
                                   @Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM File_Entity f WHERE f.fileotp = :fileotp")
    boolean existsByfileotp(@Param("fileotp") String fileotp);


    @Query("SELECT f FROM File_Entity f WHERE f.fileotp = :fileotp AND f.user.id = :userId")
    File_Entity findByFileotpAndUserId(@Param("fileotp") String fileotp,
                                       @Param("userId") int userId);



    @Query("SELECT CASE WHEN f.fileotp IS NULL THEN TRUE ELSE FALSE END " +
            "FROM File_Entity f WHERE f.id = :id")
    boolean isFileOtpNull(@Param("id") long id);



    @Transactional
    @Modifying
    @Query("UPDATE File_Entity f SET f.fileotp = NULL WHERE f.id = :fileId AND f.user.id = :userId")
    int deleteOtpByFileIdAndUserId(@Param("fileId") Long fileId,
                                   @Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("UPDATE File_Entity f SET f.downloads = f.downloads + 1 WHERE f.id = :id AND f.user.id = :userId")
    int incrementFilecountByFileIdAndUserId(@Param("id") Long id,
                                            @Param("userId") int userId);



    @Transactional
    @Modifying
    @Query("DELETE FROM File_Entity f WHERE f.id = :fileId AND f.user.id = :userId")
    int deleteByIdAndUserId(@Param("fileId") Long fileId, @Param("userId") int userId);




}