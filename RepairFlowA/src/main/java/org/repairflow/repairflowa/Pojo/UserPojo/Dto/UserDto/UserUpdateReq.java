package org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateReq(
        @NotBlank(message = "first name can not be empty") @Size(max = 60) String firstName,
        @NotBlank(message = "last name can not be empty") @Size(max = 60) String lastName,
        @NotBlank(message = "phone number can not be empty") @Size(max = 25) String phone
) {

}
