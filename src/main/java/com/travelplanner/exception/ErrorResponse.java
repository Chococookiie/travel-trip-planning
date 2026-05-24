package com.travelplanner.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    private Integer status;
    private String message;
    private String error;
    private LocalDateTime timestamp;
    private String path;
    
    public static ErrorResponse of(Integer status, String message, String error, String path) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
