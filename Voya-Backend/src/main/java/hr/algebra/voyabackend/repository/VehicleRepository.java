package hr.algebra.voyabackend.repository;

import hr.algebra.voyabackend.model.Vehicle;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    //    Available methods:
//    findAll() — get all records
//    findById(id) — get one by ID
//    save(entity) — insert or update
//    deleteById(id) — delete
//    existsById(id) — check exists
//    count() — total records

    //    Custom methods:
    boolean existsByRegistration(String registration);
    List<Vehicle> findAllByActiveTrue();
}
