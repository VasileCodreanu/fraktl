package org.java.fraktl.user_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Invalid request: 'userName' can not be empty")
    @Size(max=100, message =" Invalid request: 'userName' should be smaller then 100 chars")
    String userName
){

}
