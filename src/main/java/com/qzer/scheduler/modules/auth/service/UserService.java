package com.qzer.scheduler.modules.auth.service;

import com.qzer.scheduler.modules.auth.entity.User;

public interface UserService {

    User findByUsername(String username);

    User register(User user);

    User login(String username, String password);

    void changePassword(Long userId, String oldPassword, String newPassword);

    String generateToken(User user);

    User getUserFromToken(String token);
}
