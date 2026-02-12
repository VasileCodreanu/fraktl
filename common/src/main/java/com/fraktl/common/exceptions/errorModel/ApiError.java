package com.fraktl.common.exceptions.errorModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {

  private int status;
  private String message;
  private String debugMessage;

  @Builder.Default
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss.SSS")
  private LocalDateTime timestamp = LocalDateTime.now();

  @JsonInclude(Include.NON_NULL)
  private List<ApiSubError> subErrors;
}
