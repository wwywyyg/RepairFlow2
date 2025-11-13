package org.repairflow.repairflowa.Exception.Response;

import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserDto;

/**
 * @author guangyang
 * @date 11/11/25 AM4:14
 * @description TODO: Description
 */public record UserLoginResponse(
         String token,
         UserDto user
) {
}
