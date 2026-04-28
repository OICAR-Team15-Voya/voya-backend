package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.exception.UserNotFoundException;
import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.dto.*;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.UserRepository;
import hr.algebra.voyabackend.security.JwtUtilities;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtilities jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers the user as a NEW CLIENT.
     * First, check if the user already exists. If not, create a new user.
     * Throws ResponseStatusException (HTTP Status) if the user already exists.
     * @param dto UserRegisterDto
     * @return AuthApiResponseDto - JWT token and User information
     */
    public AuthApiResponseDto registerAsClient(UserRegisterDto dto) {
        // check if email is already in use
        if (userIsAlreadyRegistered(dto)) {
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

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return buildApiResponse(token, savedUser);
    }

    /**
     * Registers the user as a NEW DRIVER.
     * First, check if the user already exists. If not, create a new user.
     * Throws ResponseStatusException (HTTP Status) if the user already exists.
     * @param dto UserRegisterDto
     * @return AuthApiResponseDto - JWT token and User information
     */
    public AuthApiResponseDto registerAsDriver(UserRegisterDto dto) {
        // check if email is already in use
        if (userIsAlreadyRegistered(dto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + dto.getEmail());
        }
        // create new user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.DRIVER);
        user.setStatus(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return buildApiResponse(token, savedUser);
    }

    /**
     * Registers the user as a NEW ADMIN. This method must be ADMIN LEVEL ONLY in Controller.
     * First, check if the user already exists. If not, create a new user.
     * Throws ResponseStatusException (HTTP Status) if the user already exists.
     * @param dto UserRegisterDto
     * @return AuthApiResponseDto - JWT token and User information
     */
    public AuthApiResponseDto registerAsAdmin(UserRegisterDto dto) {
        // check if email is already in use
        if (userIsAlreadyRegistered(dto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + dto.getEmail());
        }
        // create new user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ADMIN);
        user.setStatus(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return buildApiResponse(token, savedUser);
    }

    /**
     * Login user. First check if user exists. Then check if the password is correct.
     * Throws ResponseStatusException (HTTP Status) if the user is not found or the password is incorrect.
     * @param dto UserLoginDto
     * @return AuthApiResponseDto - JWT token and User information
     */
    public AuthApiResponseDto login(UserLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return buildApiResponse(token, user);
    }

    /**
     * Get all users
     * @return List of UserDto
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList(); // returns unmodifiable list
    }

    /**
     * Get user by id
     * @param id user id
     * @return UserDto
     */
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
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
     * Update user. First check if user exists. Then update.
     * @param id user id
     * @param dto UserDto
     * @return UserDto
     */
    public UserDto updateUser(Integer id, UserDto dto) {

        // check if a user exists
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

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
     * Update user password. First, check if the user exists. Then update the password.
     * @param id user id
     * @param dto UpdatePasswordDto object containing the old and new password
     */
    public void updatePassword(Integer id, UpdatePasswordDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
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

    /**
     * Deactivate a user. First, check if the user exists. Then deactivate it.
     * @param id user id
     */
    public void deactivateUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(false);
        userRepository.save(user);
    }

    /**
     * Activate a user. First, check if the user exists. Then activate it.
     * @param id user id
     */
    public void activateUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(true);
        userRepository.save(user);
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

    private boolean userIsAlreadyRegistered(UserRegisterDto dto) {
        return userRepository.findByEmail(dto.getEmail()).isPresent();
    }

    /**
     * Builds the response the controller will return to frontend
     * @param token JWT token generated by the JwtUtilities class
     * @param savedUser User object saved in the database
     * @return AuthApiResponseDto object containing the token, user id, email, first name, last name, and role
     */
    private AuthApiResponseDto buildApiResponse(String token, User savedUser) {
        return new AuthApiResponseDto(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole().name()
        );
    }

    /**
     * Deletes a user from the database in a way that rewrites user data with the "deleted-" prefix.
     *
     * @param id user id
     * @param currentUserEmail
     */
    public void deleteAndForget(Integer id, String currentUserEmail) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!currentUserEmail.equals(user.getEmail())) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        user.setFirstName("deleted first name");
        user.setLastName("deleted second name");
        user.setEmail("deleted_" + id + "@email.com");
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setPhone("deleted phone number");
        user.setStatus(false);

        userRepository.save(user);
    }
}
