package hr.algebra.voyabackend.repository;

import hr.algebra.voyabackend.model.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleCategoryRepository extends JpaRepository<VehicleCategory, Integer> {

    //    Available methods:
//    findAll() — get all records
//    findById(id) — get one by ID
//    save(entity) — insert or update
//    deleteById(id) — delete
//    existsById(id) — check exists
//    count() — total records
}
