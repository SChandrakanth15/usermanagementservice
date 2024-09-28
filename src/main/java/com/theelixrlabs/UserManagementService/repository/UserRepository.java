package com.theelixrlabs.UserManagementService.repository;

import com.theelixrlabs.UserManagementService.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<UserModel, UUID> {

    // Custom method to find a user by username
    Optional<UserModel> findByUsername(String username);

    // Check if a username already exists
    boolean existsByUsername(String username);
}
