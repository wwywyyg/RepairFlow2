package org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterReq(
        @NotBlank(message = "first name can not be empty") @Size(max = 24) String firstName,
        @NotBlank(message = "last name can not be empty") @Size(max = 24) String lastName,
        @NotBlank(message = "Email can not be empty") @Size(max = 24) @Email String email,
        @NotBlank(message = "password can not be empty") @Size(min = 8 , max = 72) String password,
        @NotBlank(message = "phone number can not be empty") @Size(max = 24) String phone

) {
}
