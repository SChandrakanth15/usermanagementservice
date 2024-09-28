package com.theelixrlabs.UserManagementService.controller;

import com.theelixrlabs.UserManagementService.model.UserModel;
import com.theelixrlabs.UserManagementService.repository.UserRepository;
import com.theelixrlabs.UserManagementService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private WebClient.Builder webClientBuilder; // For WebClient

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepositor

    // Create or Update User
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserModel user) {
        try {
            UserModel savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // New endpoint to get all users except the logged-in user
    @GetMapping("/except")
    public ResponseEntity<List<UserModel>> getAllUsersExcept() {
        List<UserModel> users = userService.getAllUsersExcept();
        return ResponseEntity.ok(users);
    }

    // PUT endpoint to update the logged-in user
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserModel userUpdates) {
        try {
            UserModel updatedUser = userService.updateCurrentUser(userUpdates);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.ok("User deleted successfully."); // Return success message
    }

}