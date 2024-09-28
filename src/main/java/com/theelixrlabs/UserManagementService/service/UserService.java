package com.theelixrlabs.UserManagementService.service;

import com.theelixrlabs.UserManagementService.filter.JwtTokenFilter;
import com.theelixrlabs.UserManagementService.model.UserModel;
import com.theelixrlabs.UserManagementService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final JwtTokenFilter jwtTokenFilter;
    private final PasswordEncoder passwordEncoder;
 //   private WebClient.Builder webClientBuilder;
    @Autowired
    private UserRepository userRepository;


    // Constructor injection of PasswordEncoder
    public UserService(JwtTokenFilter jwtTokenFilter, PasswordEncoder passwordEncoder) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.passwordEncoder = passwordEncoder;
    }

    // Create or Update User
    public UserModel saveUser(UserModel user) {
        // Check if username is unique
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                List.of() // authorities
        );
    }

    // Get all users
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserModel> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    // Get user by username
    public Optional<UserModel> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Delete user by ID
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    public List<UserModel> getAllUsersExcept() {
        String username = jwtTokenFilter.getCurrentUser();
        List<UserModel> allUsers = userRepository.findAll();
        allUsers.removeIf(user -> user.getUsername().equals(username)); // Remove the logged-in user
        return allUsers;
    }

    public UserModel updateCurrentUser(UserModel userUpdates) {
        String username = jwtTokenFilter.getCurrentUser(); // Get the logged-in user's username
        UserModel currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Update fields, or leave them unchanged if not provided
        if (userUpdates.getUsername() != null && !userUpdates.getUsername().isEmpty()) {
            currentUser.setUsername(userUpdates.getUsername());
        }

        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
        }

        // Save the updated user back to the database
        return userRepository.save(currentUser);
    }

    public void deleteCurrentUser() {
        String username = jwtTokenFilter.getCurrentUser(); // Get the logged-in user's username
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Delete the user from the repository
        userRepository.delete(user);
    }

}


