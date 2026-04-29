package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.Vehicle;
import hr.algebra.voyabackend.model.VehicleCategory;
import hr.algebra.voyabackend.model.dto.VehicleCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleDto;
import hr.algebra.voyabackend.repository.VehicleCategoryRepository;
import hr.algebra.voyabackend.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;

    public VehicleService(VehicleRepository vehicleRepository, VehicleCategoryRepository vehicleCategoryRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleCategoryRepository = vehicleCategoryRepository;
    }

    /**
     * Get all vehicles
     * @return List of VehicleDto
     */
    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Get only active vehicles
     * @return List of active vehicles as List<VehicleDto>
     */
    public List<VehicleDto> getALlActiveVehicles() {
        return vehicleRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();    }

    /**
     * Get a vehicle by id
     * Throws ResponseStatusException (HTTP Status) if the vehicle is not found.
     * @param id vehicle id
     * @return VehicleDto
     */
    public VehicleDto getVehicleById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));
        return mapToDto(vehicle);
    }

    /**
     * Create a new vehicle
     * Throws ResponseStatusException (HTTP Status) if the vehicle category is not found or if the vehicle already exists.
     * @param dto VehicleCreateDto
     * @return VehicleDto
     */
    public VehicleDto createVehicle(VehicleCreateDto dto) {
        VehicleCategory category = vehicleCategoryRepository.findById(dto.getVehicleCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + dto.getVehicleCategoryId()));

        if (vehicleRepository.existsByRegistration(dto.getRegistration())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with registration already exists: " + dto.getRegistration());
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleCategory(category);
        vehicle.setName(dto.getName());
        vehicle.setManufacturer(dto.getManufacturer());
        vehicle.setModel(dto.getModel());
        vehicle.setRegistration(dto.getRegistration());

        return mapToDto(vehicleRepository.save(vehicle));
    }

    /**
     * Update a vehicle
     * Throws ResponseStatusException (HTTP Status) if the vehicle is not found or the vehicle category is not found.
     * @param id vehicle id
     * @param dto VehicleCreateDto
     * @return VehicleDto
     */
    public VehicleDto updateVehicle(Integer id, VehicleCreateDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));

        VehicleCategory category = vehicleCategoryRepository.findById(dto.getVehicleCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + dto.getVehicleCategoryId()));

        vehicle.setVehicleCategory(category);
        vehicle.setName(dto.getName());
        vehicle.setManufacturer(dto.getManufacturer());
        vehicle.setModel(dto.getModel());
        vehicle.setRegistration(dto.getRegistration());

        return mapToDto(vehicleRepository.save(vehicle));
    }

    /**
     * Deactivates a vehicle.
     * Vehicles are not deleted because they are always part of a reservation.
     * Throws ResponseStatusException (HTTP Status) if the vehicle is not found.
     * @param id vehicle id
     */
    public void deactivateVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found with id: " + id));
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    // MAPPER
    private VehicleDto mapToDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setVehicleCategoryId(vehicle.getVehicleCategory().getId());
        dto.setCategoryName(vehicle.getVehicleCategory().getName());
        dto.setName(vehicle.getName());
        dto.setManufacturer(vehicle.getManufacturer());
        dto.setModel(vehicle.getModel());
        dto.setRegistration(vehicle.getRegistration());
        dto.setActive(vehicle.getActive());
        return dto;
    }
}