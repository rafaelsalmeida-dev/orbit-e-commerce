package com.orbit.auth_service.repository;

import com.orbit.auth_service.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

}
