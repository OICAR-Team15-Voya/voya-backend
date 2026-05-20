package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.UpdatePasswordDto;
import hr.algebra.voyabackend.model.dto.UserDto;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private RestTestClient client;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        client = RestTestClient.bindToController(new UserController(userService)).build();
    }

    @Test
    void getAllUsers() {
        // Arrange
        List<UserDto> mockUsers = List.of(
                new UserDto(),
                new UserDto()
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act + Assert
        List<UserDto> responseBody = client.get()
                .uri("/voya/api/users/all")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getUserById() {
        // Arrange
        UserDto mockUser = new UserDto();
        mockUser.setId(1);
        mockUser.setEmail("test@test.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPhone("091234567");
        mockUser.setRole(Role.CLIENT);
        mockUser.setStatus(true);

        when(userService.getUserById(1)).thenReturn(mockUser);

        // Act + Assert
        UserDto responseBody = client.get()
                .uri("/voya/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
        assertEquals("test@test.com", responseBody.getEmail());
    }

    @Test
    void getUserById_notFound() {
        // Arrange
        when(userService.getUserById(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Act + Assert
        client.get()
                .uri("/voya/api/users/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void updateUserProfile() {
        // Arrange
        UserDto mockUser = new UserDto();
        mockUser.setId(1);
        mockUser.setEmail("test@test.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPhone("091234567");
        mockUser.setRole(Role.CLIENT);
        mockUser.setStatus(true);

        when(userService.updateUser(any(), any())).thenReturn(mockUser);

        // Act + Assert
        UserDto responseBody = client.put()
                .uri("/voya/api/users/1/profile")
                .body(mockUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
    }

    @Test
    void updateUserPassword() {
        // Arrange
        UpdatePasswordDto dto = new UpdatePasswordDto();
        dto.setOldPassword("oldPass");
        dto.setNewPassword("newPass");
        doNothing().when(userService).updatePassword(any(), any());

        // Act + Assert
        client.put()
                .uri("/voya/api/users/1/password")
                .body(dto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateUserPassword_wrongOldPassword() {
        // Arrange
        UpdatePasswordDto dto = new UpdatePasswordDto();
        dto.setOldPassword("wrongPass");
        dto.setNewPassword("newPass");
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password"))
                .when(userService).updatePassword(any(), any());

        // Act + Assert
        client.put()
                .uri("/voya/api/users/1/password")
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(401);
    }

    @Test
    void deactivateUser() {
        // Arrange
        doNothing().when(userService).deactivateUser(any());

        // Act + Assert
        client.put()
                .uri("/voya/api/users/1/deactivate")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deactivateUser_notFound() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(userService).deactivateUser(any());

        // Act + Assert
        client.put()
                .uri("/voya/api/users/999/deactivate")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void activateUser() {
        // Arrange
        doNothing().when(userService).activateUser(any());

        // Act + Assert
        client.put()
                .uri("/voya/api/users/1/activate")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteUser() {
        // Arrange
        doNothing().when(userService).deleteUser(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/users/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteUser_notFound() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(userService).deleteUser(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/users/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void deleteAndForget() {
        // This endpoint uses @AuthenticationPrincipal, which requires
        // full security context - not testable with bindToController()
        // Would need @SpringBootTest + @WithMockUser to test properly
    }
}