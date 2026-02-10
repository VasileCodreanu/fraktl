package org.java.fraktl.user_management.service.impl;

import org.java.fraktl.user_management.dto.UserRequest;
import org.java.fraktl.user_management.dto.UserResponse;
import org.java.fraktl.user_management.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  protected UserResponse toUserResponse(UserEntity userEntity){
    return UserResponse.builder()
        .userName(userEntity.getUserName())
        .build();
  }

  protected UserEntity toUserEntity(UserRequest userRequest){
    return UserEntity.create(userRequest.userName());
  }
}
