package org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import java.time.OffsetDateTime;


public record UserDto(
        Long id,
        @NotBlank(message = "first name can not be empty")
        String firstName,
        @NotBlank(message = "last name can not be empty")
        String lastName,
        @NotBlank(message = "Email can not be empty")
        @Email
        String email,
        @NotBlank(message = "phone number  can not be empty")
        String phone,
        Role role,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
){
    public UserDto(User user){
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getRole(), user.isActive(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
