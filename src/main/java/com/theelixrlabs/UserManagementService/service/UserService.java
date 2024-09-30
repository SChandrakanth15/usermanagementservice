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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


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
        logger.info("Saving new user with username: {}", user.getUsername());
        // Check if username is unique
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Username '{}' is already taken.", user.getUsername());
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user with username: {}", username);
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        logger.debug("User '{}' loaded successfully.", username);
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
        logger.info("Fetching all users.");
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserModel> getUserById(UUID id) {
        logger.info("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    // Get user by username
    public Optional<UserModel> getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    // Delete user by ID
    public void deleteUserById(UUID id) {
        logger.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        logger.info("User with ID '{}' deleted successfully.", id);
    }

    public List<UserModel> getAllUsersExcept() {
        String username = jwtTokenFilter.getCurrentUser();
        List<UserModel> allUsers = userRepository.findAll();
        allUsers.removeIf(user -> user.getUsername().equals(username)); // Remove the logged-in user
        return allUsers;
    }

    public UserModel updateCurrentUser(UserModel userUpdates) {
        String username = jwtTokenFilter.getCurrentUser();
        logger.info("Updating logged-in user: {}", username);
        UserModel currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Update fields, or leave them unchanged if not provided
        if (userUpdates.getUsername() != null && !userUpdates.getUsername().isEmpty()) {
            logger.info("Updating username to: {}", userUpdates.getUsername());
            currentUser.setUsername(userUpdates.getUsername());
        }

        if (userUpdates.getPassword() != null && !userUpdates.getPassword().isEmpty()) {
            logger.info("Updating password for user: {}", username);
            currentUser.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
        }

        // Save the updated user back to the database
        return userRepository.save(currentUser);
    }

    public void deleteCurrentUser() {
        String username = jwtTokenFilter.getCurrentUser();
        logger.info("Deleting logged-in user: {}", username);
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Delete the user from the repository
        userRepository.delete(user);
        logger.info("User '{}' deleted successfully.", username);
    }

}


