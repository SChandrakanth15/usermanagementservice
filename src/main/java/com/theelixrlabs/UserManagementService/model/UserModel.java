    package com.theelixrlabs.UserManagementService.model;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import jakarta.validation.constraints.NotEmpty;

    import java.util.UUID;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Document(collection = "users")
    public class UserModel {
        @Id
        private UUID id;

        @NotEmpty(message = "Username cannot be empty")
        private String username;

        @NotEmpty(message = "Password cannot be empty")
        private String password;

        private boolean active = true; // Already set by default
    }

