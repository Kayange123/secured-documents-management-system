package dev.kayange.sdms.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(String time, int code, String path, String message, HttpStatus status, String exception, Map<?,?> data) {
}
