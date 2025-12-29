package com.web.repository;

import com.web.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "select u.* from users u",nativeQuery = true)
    Page<User> findAll(Pageable pageable);

    @Query(value = "select u.* from user u where u.username = ?1 and u.password = ?2", nativeQuery = true)
    Optional<User> findByUsernameAndPassword(String username, String password);

    @Query(value = "select u.* from user u where u.username = ?1", nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query(value = "select u.* from user u where u.role = ?1",nativeQuery = true)
    List<User> getUserByRole(String role);

    @Query("select u from User u where u.fullname like ?1 or u.idCard like ?1 or u.phone like ?1")
    List<User> findByParam(String param);

//    @Query("select u from User u where u.phone like ?1")
    boolean existsByPhone(String phone);

//    @Query("select u from User u where u.idCard like ?1")
    boolean existsByIdCard(String idCard);

    @Query("SELECT COUNT(u) FROM User u")
    long countTotalRegisteredUsers();
}
