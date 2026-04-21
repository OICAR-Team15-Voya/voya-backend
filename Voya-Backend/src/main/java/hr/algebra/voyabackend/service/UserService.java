package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.dto.UserDto;
import hr.algebra.voyabackend.model.dto.UserRegisterDto;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all users
     * @return List of UserDto
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get user by id
     * @param id user id
     * @return UserDto
     */
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDto(user);
    }

    /**
     * Get user by email.
     * Throws ResponseStatusException (HTTP Status) if the user is not found.
     * @param email user email
     * @return UserDto
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

        return mapToDto(user);
    }

    /**
     * Register new user. First, check if email is already in use.
     * Throws ResponseStatusException (HTTP Status) if user already exists.
     * @param dto UserRegisterDto
     * @return UserDto
     */
    public UserDto register(UserRegisterDto dto) {

        // check if email is already in use
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + dto.getEmail());
        }

        // create new user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CLIENT);
        user.setStatus(true);

        return mapToDto(userRepository.save(user));
    }

    /**
     * Update user. First check if user exists. Then update.
     * @param id user id
     * @param dto UserDto
     * @return UserDto
     */
    public UserDto updateUser(Integer id, UserDto dto) {

        // check if a user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // update user
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());

        return mapToDto(userRepository.save(user));
    }

    /**
     * Delete a user by id. First check if the user exists. Then delete it.
     * Throws ResponseStatusException (HTTP Status) if the user is not found.
     * @param id user id
     */
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);        }
        userRepository.deleteById(id);
    }

    // MAPPER
    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
