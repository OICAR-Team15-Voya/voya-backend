package hr.algebra.voyabackend.repository;

import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    //    Available methods:
//    findAll() — get all records
//    findById(id) — get one by ID
//    save(entity) — insert or update
//    deleteById(id) — delete
//    existsById(id) — check exists
//    count() — total records

    //    Custom methods:
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
