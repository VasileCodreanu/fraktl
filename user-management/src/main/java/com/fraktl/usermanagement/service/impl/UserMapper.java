package com.fraktl.usermanagement.service.impl;

import com.fraktl.usermanagement.dto.UserRequest;
import com.fraktl.usermanagement.dto.UserResponse;
import com.fraktl.usermanagement.entity.UserEntity;
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
