package com.sigp.dao;

import com.sigp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAOInterface {
    User create(User user);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean update(User user);

    boolean deleteByUsername(String username);
}
