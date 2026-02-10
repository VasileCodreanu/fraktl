package org.java.fraktl.user_management.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.java.fraktl.user_management.dto.UserRequest;
import org.java.fraktl.user_management.dto.UserResponse;
import org.java.fraktl.common.api.ApiResponse;
import org.java.fraktl.user_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix.v1}/user")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
      @Valid @RequestBody UserRequest userRequest){

    UserResponse response = userService.createUser(userRequest);

    ApiResponse<UserResponse> apiResponse = ApiResponse.success(response);
    return new ResponseEntity<>(apiResponse, CREATED);
  }
}
