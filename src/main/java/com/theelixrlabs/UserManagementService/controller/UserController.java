package com.theelixrlabs.UserManagementService.controller;

import com.theelixrlabs.UserManagementService.constants.ApiPathsConstant;
import com.theelixrlabs.UserManagementService.constants.UserManagementServiceConstant;
import com.theelixrlabs.UserManagementService.model.UserModel;
import com.theelixrlabs.UserManagementService.repository.UserRepository;
import com.theelixrlabs.UserManagementService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@RequestMapping(ApiPathsConstant.USERS_BASE_ENDPOINT)
@CrossOrigin(origins = "https://exr-138-frontend.nicepebble-15cceb5b.southindia.azurecontainerapps.io")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private UserService userService;
    @Autowired
    private WebClient.Builder webClientBuilder; // For WebClient

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepositor

    @PostMapping(ApiPathsConstant.REGISTER_A_USER_ENDPOINT)
    public ResponseEntity<?> createUser(@RequestBody UserModel user) {
        try {
            logger.info("Registering new user: {}", user.getUsername());
            UserModel savedUser = userService.saveUser(user);
            logger.info("User '{}' registered successfully.", savedUser.getUsername());
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Error while registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    // Get all users
//    @GetMapping("/all")
//    public ResponseEntity<List<UserModel>> getAllUsers() {
//        logger.info("Fetching all users.");
//        List<UserModel> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }

    // Get all users except the logged-in user
    @GetMapping(ApiPathsConstant.GET_ALL_USERS_EXCEPT_ENDPOINT)
    public ResponseEntity<List<UserModel>> getAllUsersExcept() {
        logger.info("Fetching all users except the logged-in user.");
        List<UserModel> users = userService.getAllUsersExcept();
        return ResponseEntity.ok(users);
    }

    // Update the logged-in user
    @PutMapping(ApiPathsConstant.UPDATE_A_USER_ENDPOINT)
    public ResponseEntity<?> updateUser(@RequestBody UserModel userUpdates) {
        try {
            logger.info("Updating the current user.");
            UserModel updatedUser = userService.updateCurrentUser(userUpdates);
            logger.info("User updated successfully.");
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete the current user
    @DeleteMapping(ApiPathsConstant.DELETE_A_USER_ENDPOINT)
    public ResponseEntity<String> deleteCurrentUser() {
        logger.info("Deleting the current user.");
        userService.deleteCurrentUser();
        logger.info("User deleted successfully.");
        return ResponseEntity.ok(UserManagementServiceConstant.USER_DELETED_SUCCESSFULLY);
    }

}