package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.VehicleCategory;
import hr.algebra.voyabackend.model.dto.VehicleCategoryCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleCategoryDto;
import hr.algebra.voyabackend.repository.VehicleCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleCategoryService {

    private final VehicleCategoryRepository vehicleCategoryRepository;

    public VehicleCategoryService(VehicleCategoryRepository vehicleCategoryRepository) {
        this.vehicleCategoryRepository = vehicleCategoryRepository;
    }

    /**
     * Get all vehicle categories
     * @return List of VehicleCategoryDto
     */
    public List<VehicleCategoryDto> getAllCategories() {
        return vehicleCategoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Get vehicle category by id
     * @param id category id
     * @return VehicleCategoryDto
     */
    public VehicleCategoryDto getCategoryById(Integer id) {
        VehicleCategory category = vehicleCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + id));
        return mapToDto(category);
    }

    /**
     * Create a new vehicle category
     * @param dto VehicleCategoryCreateDto
     * @return VehicleCategoryDto
     */
    public VehicleCategoryDto createCategory(VehicleCategoryCreateDto dto) {
        if (vehicleCategoryRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists: " + dto.getName());
        }

        VehicleCategory category = new VehicleCategory();
        category.setName(dto.getName());

        return mapToDto(vehicleCategoryRepository.save(category));
    }

    /**
     * Update a vehicle category
     * @param id category id
     * @param dto VehicleCategoryCreateDto
     * @return VehicleCategoryDto
     */
    public VehicleCategoryDto updateCategory(Integer id, VehicleCategoryCreateDto dto) {
        VehicleCategory category = vehicleCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + id));

        category.setName(dto.getName());
        return mapToDto(vehicleCategoryRepository.save(category));
    }

    /**
     * Delete a vehicle category
     * @param id category id
     */
    public void deleteCategory(Integer id) {
        VehicleCategory category = vehicleCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + id));

        if (!category.getVehicles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete category because it has vehicles assigned to it");
        }

        vehicleCategoryRepository.deleteById(id);
    }

    // MAPPER
    private VehicleCategoryDto mapToDto(VehicleCategory category) {
        VehicleCategoryDto dto = new VehicleCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
