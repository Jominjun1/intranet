package com.example.tag_dev.Config;

import com.example.tag_dev.USER.Model.User;
import com.example.tag_dev.USER.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PasswordEncryptor {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void encryptAllAdminPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getPassword().startsWith("$2a$")) { // 이미 암호화되지 않은 경우만
                String encryptedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encryptedPassword);
                userRepository.save(user);
            }
        }
    }

    public void encryptAdminPassword(String LoginId) {
        User user = userRepository.findByLoginId(LoginId).orElse(null);
        if (user != null && !user.getPassword().startsWith("$2a$")) {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        }
    }
}
