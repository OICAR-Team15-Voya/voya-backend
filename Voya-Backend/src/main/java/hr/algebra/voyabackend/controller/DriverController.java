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
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/all")
    public ResponseEntity<List<DriverDto>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<DriverDto> createDriver(@RequestBody DriverCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriver(@PathVariable Integer id, @RequestBody DriverProfileUpdateDto dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }
}
