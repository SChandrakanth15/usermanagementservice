package com.theelixrlabs.UserManagementService.model;

import com.theelixrlabs.UserManagementService.constants.UserManagementServiceConstant;
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
@Document(collection = UserManagementServiceConstant.USERMODEL_COLLECTION_NAME)
public class UserModel {
    @Id
    private UUID id;

    @NotEmpty(message = UserManagementServiceConstant.USERNAME_CANNOT_BE_EMPTY)
    private String username;

    @NotEmpty(message = UserManagementServiceConstant.PASSWORD_CANNOT_BE_EMPTY)
    private String password;

    private boolean active = true; // Already set by default
}

