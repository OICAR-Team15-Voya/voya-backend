package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.VehicleCategoryCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleCategoryDto;
import hr.algebra.voyabackend.service.VehicleCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voya/api/vehicle-categories") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
public class VehicleCategoryController {

    private final VehicleCategoryService vehicleCategoryService;

    /**
     * Returns all vehicle categories from the database.
     * URL is: /voya/api/vehicle-categories
     * @return List<VehicleCategoryDto> 200 if OK
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<VehicleCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(vehicleCategoryService.getAllCategories());
    }

    /**
     * Returns vehicle category by id.
     * URL is: /voya/api/vehicle-categories/{id}
     * @param id of the vehicle category
     * @return VehicleCategoryDto 200 if OK
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleCategoryDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleCategoryService.getCategoryById(id));
    }

    /**
     * Creates a new vehicle category.
     * Throws HttpStatus.CONFLICT if the category already exists.
     * URL is: /voya/api/vehicle-categories
     * @param dto with the new category data
     * @return VehicleCategoryDto 201 if Created
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VehicleCategoryDto> createCategory(@RequestBody VehicleCategoryCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleCategoryService.createCategory(dto));
    }

    /**
     * Updates a vehicle category.
     * Throws HttpStatus.NOT_FOUND if the category does not exist.
     * @param id of the vehicle category
     * @param dto with new data
     * @return VehicleCategoryDto 200 if OK
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleCategoryDto> updateCategory(@PathVariable Integer id, @RequestBody VehicleCategoryCreateDto dto) {
        return ResponseEntity.ok(vehicleCategoryService.updateCategory(id, dto));
    }

    /**
     * Deletes a vehicle category.
     * Throws HttpStatus.NOT_FOUND if the category does not exist.
     * Throws HttpStatus.CONFLICT if the category has vehicles assigned to it and prevents deletion.
     * @param id of the vehicle category
     * @return 200 if OK
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        vehicleCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}