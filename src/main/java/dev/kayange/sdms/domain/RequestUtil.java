package dev.kayange.sdms.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kayange.sdms.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RequestUtil {

    private static final BiConsumer<HttpServletResponse, ApiResponse> writeResponse = (httpServletResponse, apiResponse)->{
       try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, apiResponse);
            outputStream.flush();
       } catch (Exception e) {
           throw new ApiException("Something went wrong");
       }
    };

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, status)->{
        if(status.isSameCodeAs(FORBIDDEN)) return "You do not have enough permission to perform this";
        if(status.isSameCodeAs(UNAUTHORIZED)) return  "You are not authenticated to perform this";
        if(exception instanceof DisabledException || exception instanceof LockedException || exception instanceof BadCredentialsException
            || exception instanceof CredentialsExpiredException || exception instanceof ApiException
        ){
            return exception.getMessage();
        }
        if (status.is5xxServerError()) return "Internal Server Error Occurred";
        else return "Something went wrong. Please try again.";
    };

    public static ApiResponse getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status){
        return new ApiResponse(LocalDateTime.now().toString(), status.value(), request.getRequestURI(), message, HttpStatus.valueOf(status.value()), EMPTY, data);
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e){
        if(e instanceof AccessDeniedException){
            ApiResponse apiResponse = getErrorResponse(request, response, e, FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }
    }

    private static ApiResponse getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e, HttpStatus httpStatus) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());


        return new ApiResponse(LocalDateTime.now().toString(), httpStatus.value(), request.getRequestURI(), errorReason.apply(e, httpStatus),
                HttpStatus.valueOf(httpStatus.value()), getRootCauseMessage(e), emptyMap());
    }
}
