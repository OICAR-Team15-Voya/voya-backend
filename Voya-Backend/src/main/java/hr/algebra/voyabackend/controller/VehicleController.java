package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.VehicleCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleDto;
import hr.algebra.voyabackend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("voya/api/vehicles") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Returns all vehicles from the database.
     * URL is: /voya/api/vehicles/all
     * @return List<VehicleDto>
     */
    @GetMapping("/all")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    /**
     * Returns active vehicles from the database.
     * Call this endpoint if you want to retrieve only active vehicles.
     * URL is: /voya/api/vehicles/all-active
     * @return List<VehicleDto>
     */
    @GetMapping("/all-active")
    public ResponseEntity<List<VehicleDto>> getAllActiveVehicles() {
        return ResponseEntity.ok(vehicleService.getALlActiveVehicles());
    }

    /**
     * Returns vehicle by id.
     * URL is: /voya/api/vehicles/{id}
     * @param id vehicle id
     * @return VehicleDto
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * Creates a new vehicle.
     * URL is: /voya/api/vehicles
     * @param dto with the new vehicle data
     * @return VehicleDto 201 if Created
     */
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(dto));
    }

    /**
     * Updates a vehicle.
     * URL is: /voya/api/vehicles/{id}
     * @param id vehicle id
     * @param dto with the new vehicle data
     * @return
     */
    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable Integer id, @RequestBody VehicleCreateDto dto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }

    /**
     * Deletes a vehicle.
     * URL is: /voya/api/vehicles/{id}
     * @param id vehicle id
     * @return 200 if OK
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer id) {
        vehicleService.deactivateVehicle(id);
        return ResponseEntity.ok().build();
    }
}