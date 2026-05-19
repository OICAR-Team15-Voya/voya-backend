package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.DriverCreateDto;
import hr.algebra.voyabackend.model.dto.DriverDto;
import hr.algebra.voyabackend.model.dto.DriverProfileUpdateDto;
import hr.algebra.voyabackend.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voya/api/drivers") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DriverController {

    private final DriverService driverService;

    /**
     * Returns all drivers from the database.
     * URL is: /voya/api/drivers/all
     * @return List<DriverDto> 200 if OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<DriverDto>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    /**
     * Returns driver by id.
     * URL is: /voya/api/drivers/{id}
     * @param id driver id
     * @return DriverDto 200 if OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    /**
     * Creates a new driver and user.
     * URL is: /voya/api/drivers
     * @param dto DriverCreateDto
     * @return DriverDto 201 if Created
     */
    @PostMapping
    public ResponseEntity<DriverDto> createDriver(@RequestBody DriverCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(dto));
    }

    /**
     * Updates driver profile.
     * Cascade updates user profile.
     * URL is: /voya/api/drivers/{id}
     * @param id driver id
     * @param dto DriverProfileUpdateDto
     * @return DriverDto 200 if OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriver(@PathVariable Integer id, @RequestBody DriverProfileUpdateDto dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    /**
     * Deletes driver profile.
     * Cascade deletes user profile.
     * URL is: /voya/api/drivers/{id}
     * @param id driver id
     * @return 200 if OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }
}
