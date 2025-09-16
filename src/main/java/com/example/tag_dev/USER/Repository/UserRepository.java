package com.example.tag_dev.USER.Repository;

import com.example.tag_dev.USER.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.loginId = :loginId")
    Optional<User> findByLoginId(String loginId);

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findByUserId(Long userId);

    @Query("DELETE FROM User u WHERE u.loginId = :loginId")
    void deleteByLoginId(@Param("loginId") String loginId);

    @Query("SELECT u.loginId FROM User u WHERE u.userName = :user_name AND (u.userEmail = :user_email OR u.userPhoneNum = :user_phone_num)")
    Optional<User> findByNameAndEmailOrPhone(@Param("user_name") String user_name, @Param("user_email") String user_email, @Param("user_phone_num") String user_phone_num);

    @Query("SELECT u.password FROM User u WHERE u.userName = :user_name AND u.loginId = :login_id AND u.userEmail = :user_email AND u.userPhoneNum = :user_phone_num")
    Optional<User> findByUserNameAndLoginIdAndUserEmailAndUserPhoneNum(String user_name, String login_id, String user_email, String user_phone_num);
}
