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
@RequestMapping("/voya/api/auth") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    /**
     * Registers a new user as a client.
     * Call this endpoint if you want to register a client.
     * URL is: /voya/api/auth/clientRegister
     * @param dto UserRegisterDto (email, password, firstName, lastName, phone)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/clientRegister")
    public ResponseEntity<AuthApiResponseDto> clientRegister(@RequestBody UserRegisterDto dto) {
        AuthApiResponseDto response = userService.registerAsClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Registers a new user as a driver
     * Call this endpoint if you want to register a driver.
     * URL is: /voya/api/auth/driverRegister
     * @param dto UserRegisterDto (email, password, firstName, lastName, phone)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/driverRegister")
    public ResponseEntity<AuthApiResponseDto> driverRegister(@RequestBody UserRegisterDto dto) {
        AuthApiResponseDto response = userService.registerAsDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registers a new user as an admin
     * Call this endpoint if you want to register an admin.
     * URL is: /voya/api/auth/adminRegister
     * @param dto UserRegisterDto (email, password, firstName, lastName, phone)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/adminRegister")
    public ResponseEntity<AuthApiResponseDto> adminRegister(@RequestBody UserRegisterDto dto) {
        AuthApiResponseDto response = userService.registerAsAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login endpoint.
     * URL is: /voya/api/auth/login
     * @param dto UserLoginDto (email, password)
     * @return AuthApiResponseDto -> JSON with JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthApiResponseDto> login(@RequestBody UserLoginDto dto) {
        AuthApiResponseDto response = userService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
