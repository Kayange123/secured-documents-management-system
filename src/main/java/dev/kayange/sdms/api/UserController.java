package dev.kayange.sdms.api;

import dev.kayange.sdms.domain.ApiResponse;
import dev.kayange.sdms.dto.UserRequest;
import dev.kayange.sdms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static dev.kayange.sdms.domain.RequestUtil.getResponse;
import static java.util.Collections.emptyMap;

@RestController
@RequestMapping(path = {"/auth"})
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createNewUser(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request){
        userService.createNewUser(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getPassword());
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account Created Successfully. We've sent you Email to verify your Account", HttpStatus.CREATED));
    }

    @GetMapping("/verify/account")
    public ResponseEntity<ApiResponse> verifyAccount(@RequestParam("token") String token, HttpServletRequest request){
        userService.verifyAccount(token);
        return ResponseEntity.created(getUri()).body(getResponse(request, emptyMap(), "Account Verified Successfully", HttpStatus.OK));

    }
    private URI getUri() {
        return URI.create("");
    }
}
