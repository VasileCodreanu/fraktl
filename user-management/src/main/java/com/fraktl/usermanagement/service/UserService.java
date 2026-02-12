package com.fraktl.usermanagement.service;

import com.fraktl.usermanagement.dto.UserRequest;
import com.fraktl.usermanagement.dto.UserResponse;

public interface UserService {
  UserResponse createUser(UserRequest userRequest);
}
