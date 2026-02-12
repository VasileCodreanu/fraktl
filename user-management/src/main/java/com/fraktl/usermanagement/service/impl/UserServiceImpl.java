package com.fraktl.usermanagement.service.impl;

import lombok.RequiredArgsConstructor;
import com.fraktl.usermanagement.dto.UserRequest;
import com.fraktl.usermanagement.dto.UserResponse;
import com.fraktl.usermanagement.entity.UserEntity;
import com.fraktl.usermanagement.repository.UserRepository;
import com.fraktl.usermanagement.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponse createUser(UserRequest userRequest){
    UserEntity appUser = userMapper.toUserEntity(userRequest);
    UserEntity savedUser = userRepository.save(appUser);

    return userMapper.toUserResponse(savedUser);
  }

}
