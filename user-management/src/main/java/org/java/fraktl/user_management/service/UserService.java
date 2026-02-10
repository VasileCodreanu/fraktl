package org.java.fraktl.user_management.service;

import org.java.fraktl.user_management.dto.UserRequest;
import org.java.fraktl.user_management.dto.UserResponse;

public interface UserService {
  UserResponse createUser(UserRequest userRequest);
}
