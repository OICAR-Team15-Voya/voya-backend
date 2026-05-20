package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.AuthApiResponseDto;
import hr.algebra.voyabackend.model.dto.UserRegisterDto;
import hr.algebra.voyabackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    private RestTestClient client;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        client = RestTestClient.bindToController(new AuthenticationController(userService)).build();
            // tests pass without an authorization header because of the bindToController
    }

    @Test
    void clientRegister_success() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        // tell Mockito what to return when registerAsClient is called
        AuthApiResponseDto mockResponse = new AuthApiResponseDto("token", 1, "test@test.com", "Test", "User", "CLIENT");
        when(userService.registerAsClient(any())).thenReturn(mockResponse);

        AuthApiResponseDto responseBody = client.post()
                .uri("/voya/api/auth/clientRegister")
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AuthApiResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getToken());
    }

    @Test
    void clientCannotRegister_whenEmailAlreadyInUse() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        when(userService.registerAsClient(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use"));

        client.post()
                .uri("/voya/api/auth/clientRegister")
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(409); // 409 Conflict
    }

    @Test
    void adminRegister_success() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        AuthApiResponseDto mockResponse = new AuthApiResponseDto("token", 1, "test@test.com", "Test", "User", "ADMIN");

        when(userService.registerAsAdmin(any())).thenReturn(mockResponse);

        AuthApiResponseDto responseBody = client.post()
                .uri("/voya/api/auth/adminRegister")
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AuthApiResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getToken());
        assertEquals("ADMIN", responseBody.getRole());
    }

    @Test
    void adminRegister_whenEmailInUse() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("admin@test.com");
        dto.setPassword("admin123");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        when(userService.registerAsClient(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use"));

        client.post()
                .uri("/voya/api/auth/clientRegister")
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(409); // 409 Conflict
    }

    @Test
    void login_success() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        AuthApiResponseDto mockResponse = new AuthApiResponseDto("token", 1, "test@test.com", "Test", "User", "CLIENT");
        when(userService.login(any())).thenReturn(mockResponse);

        AuthApiResponseDto responseBody = client.post()
                .uri("/voya/api/auth/login")
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthApiResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getToken());
    }

    @Test
    void login_failed_wrongCredentials(){
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPhone("091234567");

        when(userService.login(any())).
                thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        client.post()
                .uri("/voya/api/auth/login")
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(401);
    }
}