package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.UpdatePasswordDto;
import hr.algebra.voyabackend.model.dto.UserDto;
import hr.algebra.voyabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voya/api/users") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns all users from the database.ž
     * URL is: /voya/api/users/all
     * @return List<UserDto>
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Returns user by id.
     * URL is: /voya/api/users/{id}
     * @param id user id
     * @return UserDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Updates user information provided in the request body.
     * URL is: /voya/api/users/{id}/profile
     * @param id user id - path variable to identify the user
     * @param dto UserDto from the request body - should contain the new information
     * @return UserDto with updated information
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @RequestBody UserDto dto) {
        return ResponseEntity.ok (userService.updateUser(id, dto));
    }

    /**
     * Updates user password.
     * @param id user id
     * @param passwordDto UpdatePasswordDto object containing the old and new password
     * @return response message whether the user was deleted or not
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Integer id, @RequestBody UpdatePasswordDto passwordDto) {
        userService.updatePassword(id, passwordDto);
        return ResponseEntity.ok("Password updated");
    }

    /**
     * Deactivates user profile.
     * URL is: /voya/api/users/{id}/deactivate
     * @param id user id
     * @return response message whether the user was deleted or not
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated");
    }

    /**
     * Activates user profile.
     * URL is: /voya/api/users/{id}/activate
     * @param id user id
     * @return response message whether the user was deleted or not
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Integer id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated");
    }

    /**
     * Deletes user profile.
     * @param id user id
     * @return response message whether the user was deleted or not
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
