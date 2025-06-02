package org.java.fraktl.model.response.short_url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShortenUrlRequest(
    @NotBlank(message = "Invalid Request: 'originalUrl' is empty")
    @NotNull(message = "Invalid Request: 'originalUrl' is NULL")
    @Size(min=7, max = 1000, message = "'originalUrl'-size should be between 7 and 1000.")
    String originalUrl
) {}