package org.repairflow.repairflowa.Services.UserServices;



import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserServices {

    UserDto createUser(UserRegisterReq userRegisterReq);
    UserDto getUser(Long id);
    List<UserDto> getAllUsers();
    Page<UserDto> listUsers(int page, int pageSize);
//    UserDto updateUserSelf(Long id, UserUpdateReq userUpdateReq);
    UserDto updateUserAdmin(Long id, UserUpdateAdmin userUpdateAdmin);
    void deleteUser(Long id);
    UserDto userLogin(UserLoginReq userLoginReq);
}
