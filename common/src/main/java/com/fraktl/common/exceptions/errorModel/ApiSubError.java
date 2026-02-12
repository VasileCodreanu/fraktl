package com.fraktl.common.exceptions.errorModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiSubError {

  private String field;
  private Object rejectedValue;
  private String message;
}
