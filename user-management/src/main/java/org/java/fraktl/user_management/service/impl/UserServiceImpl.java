package org.java.fraktl.user_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.java.fraktl.user_management.dto.UserRequest;
import org.java.fraktl.user_management.dto.UserResponse;
import org.java.fraktl.user_management.entity.UserEntity;
import org.java.fraktl.user_management.repository.UserRepository;
import org.java.fraktl.user_management.service.UserService;
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
