package org.repairflow.repairflowa.Controller;



import jakarta.validation.Valid;
import org.repairflow.repairflowa.Exception.ApiResponse;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserDto;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserRegisterReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateAdmin;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateReq;
import org.repairflow.repairflowa.UserServices.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/user")
public class UserAuthController {
    @Autowired
    private UserServices userServices;


    // Create User
    @PostMapping("/register")  // localhost:8080/auth/user/register
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody  UserRegisterReq userRegisterReq) {
        UserDto createdUser = userServices.createUser(userRegisterReq);
        return new ResponseEntity<>(ApiResponse.success("User Created Successfully", createdUser), HttpStatus.OK);
    }

    // read user
    // read one
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto userDto = userServices.getUser(id);
        return  ResponseEntity.ok(ApiResponse.success(userDto));
    }

    // read by List
    @GetMapping("/list") //http://localhost:8080/auth/user/list?page=0&size=5
    public ResponseEntity<ApiResponse<Page<UserDto>>> listUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<UserDto> userDto = userServices.listUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }



    // Update User
    @PutMapping("/update/{id}") //http://localhost:8080/auth/user/update/{id}
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateReq userUpdateReq) {
        UserDto userDto = userServices.updateUserSelf(id, userUpdateReq);
        return ResponseEntity.ok(ApiResponse.success("User Updated Successfully", userDto));
    }

    @PatchMapping("/update/{id}/admin")
    public ResponseEntity<ApiResponse<UserDto>> updateUserAdmin(@PathVariable Long id, @RequestBody @Valid UserUpdateAdmin userUpdateAdmin) {
        UserDto userDto = userServices.updateUserAdmin(id, userUpdateAdmin);
        return ResponseEntity.ok(ApiResponse.success("admin Updated Successfully", userDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userServices.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User Delete Success!",null));
    }

}
