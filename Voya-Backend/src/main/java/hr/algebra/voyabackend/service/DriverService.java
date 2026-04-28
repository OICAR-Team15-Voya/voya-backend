package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.Driver;
import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.dto.DriverCreateDto;
import hr.algebra.voyabackend.model.dto.DriverDto;
import hr.algebra.voyabackend.model.dto.DriverProfileUpdateDto;
import hr.algebra.voyabackend.model.enums.Role;
import hr.algebra.voyabackend.repository.DriverRepository;
import hr.algebra.voyabackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all drivers
     * @return List of DriverDto
     */
    public List<DriverDto> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Get driver by id
     * @param id driver id
     * @return DriverDto
     */
    public DriverDto getDriverById(Integer id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found with id: " + id));
        return mapToDto(driver);
    }

    /**
     * Create a new driver
     * First check if the email is already in use. If so, throw an exception.
     * @param dto DriverCreateDto
     * @return DriverDto
     */
    @Transactional
    public DriverDto createDriver(DriverCreateDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + dto.getEmail());
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.DRIVER);
        user.setStatus(true);
        User savedUser = userRepository.save(user);

        Driver driver = new Driver();
        driver.setUser(savedUser);
        driver.setLicenseValidUntil(dto.getLicenseValidUntil());

        Driver savedDriver = driverRepository.save(driver);
        return mapToDto(savedDriver);
    }

    /**
     * Update driver profile information
     * First, check if the driver exists. If not, throw an exception.
     * @param id driver id
     * @param dto DriverProfileUpdateDto
     * @return DriverDto
     */
    public DriverDto updateDriver(Integer id, DriverProfileUpdateDto dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found with id: " + id));

        User user = driver.getUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userRepository.save(user);

        driver.setLicenseValidUntil(dto.getLicenseValidUntil());
        return mapToDto(driverRepository.save(driver));
    }

    /**
     * Delete a driver by id
     * @param id driver id
     */
    public void deleteDriver(Integer id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found with id: " + id));

        userRepository.deleteById(driver.getUser().getId()); // cascades to Driver automatically
    }

    // MAPPER
    private DriverDto mapToDto(Driver driver) {
        DriverDto dto = new DriverDto();
        dto.setId(driver.getId());
        dto.setUserId(driver.getUser().getId());
        dto.setFirstName(driver.getUser().getFirstName());
        dto.setLastName(driver.getUser().getLastName());
        dto.setEmail(driver.getUser().getEmail());
        dto.setPhone(driver.getUser().getPhone());
        dto.setLicenseValidUntil(driver.getLicenseValidUntil());
        return dto;
    }
}
