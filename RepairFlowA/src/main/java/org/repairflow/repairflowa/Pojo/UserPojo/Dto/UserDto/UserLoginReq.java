package org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginReq(
        @NotBlank(message = "Please enter email to login") @Size(min = 3, max = 50)  @Email  String email,
        @NotBlank(message = "Please Enter password to login") @Size(min = 3, max = 50)   String password
        ) {
}
