package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.dto.AuthApiResponseDto;
import hr.algebra.voyabackend.model.dto.UserLoginDto;
import hr.algebra.voyabackend.model.dto.UserRegisterDto;
import hr.algebra.voyabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/voya/api/auth") // please use /voya/api convention for all endpoints
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    /**
     * Registers a new user as a client.
     * Call this endpoint if you want to register a client.
     * @param dto UserRegisterDto (email, password, firstName, lastName, phone)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/clientRegister")
    public ResponseEntity<AuthApiResponseDto> clientRegister(@RequestBody UserRegisterDto dto) {
        AuthApiResponseDto response = userService.registerAsClient(dto);
        return ResponseEntity.ok(response);
    }


    /**
     * Registers a new user as a driver
     * Call this endpoint if you want to register a driver.
     * @param dto UserRegisterDto (email, password, firstName, lastName, phone)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/driverRegister")
    public ResponseEntity<AuthApiResponseDto> driverRegister(@RequestBody UserRegisterDto dto) {
        AuthApiResponseDto response = userService.registerAsDriver(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Login endpoint.
     * @param dto UserLoginDto (email, password)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthApiResponseDto> login(@RequestBody UserLoginDto dto) {
        AuthApiResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }


}
