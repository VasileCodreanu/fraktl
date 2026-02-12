package com.fraktl.urlmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
 import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
    @NotBlank(message = "Invalid Request: 'originalUrl' is empty")
    @NotNull(message = "Invalid Request: 'originalUrl' is NULL")
    @Size(min = 7, max = 2048, message = "'originalUrl'-size should be between 7 and 2048 characters.")
    @URL(message = "Invalid URL format")
    String originalUrl
) {

}