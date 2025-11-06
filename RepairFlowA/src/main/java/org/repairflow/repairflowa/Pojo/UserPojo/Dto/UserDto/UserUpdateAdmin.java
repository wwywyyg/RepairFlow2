package org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto;


import jakarta.validation.constraints.NotNull;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;

public record UserUpdateAdmin(
        @NotNull Boolean isActive,
        @NotNull Role role
) {

}
