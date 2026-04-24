package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.exception.UserNotFoundException;
import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.dto.UserDto;
import hr.algebra.voyabackend.model.dto.UserLoginDto;
import hr.algebra.voyabackend.model.dto.UserRegisterDto;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.UserRepository;
import hr.algebra.voyabackend.security.JwtUtilities;
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
     * @return JWT token for immediate login
     */
    public String registerAsClient(UserRegisterDto dto) {
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
        return jwtService.generateToken(savedUser);
    }

    /**
     * Registers the user as a NEW DRIVER.
     * First, check if the user already exists. If not, create a new user.
     * Throws ResponseStatusException (HTTP Status) if the user already exists.
     * @param dto UserRegisterDto
     * @return JWT token for immediate login
     */
    public String registerAsDriver(UserRegisterDto dto) {
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
        return jwtService.generateToken(savedUser);
    }

    /**
     * Login user. First check if user exists. Then check if the password is correct.
     * Throws ResponseStatusException (HTTP Status) if the user is not found or the password is incorrect.
     * @param dto UserLoginDto
     * @return JWT token
     */
    public String login(UserLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return jwtService.generateToken(user);
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
     * Update user password. First check if the user exists. Then update the password.
     * @param email user email
     * @param newPassword new password
     */
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
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
}
