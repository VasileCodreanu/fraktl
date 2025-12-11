package org.java.fraktl.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
  ResponseStatus status,
  T data
) {

  public enum ResponseStatus {
    SUCCESS, ERROR
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(ResponseStatus.SUCCESS, data);
  }

  public static <T> ApiResponse<T> error(T errorData) {
    return new ApiResponse<>(ResponseStatus.ERROR, errorData);
  }

}
