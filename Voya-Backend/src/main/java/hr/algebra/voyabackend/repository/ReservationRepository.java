package hr.algebra.voyabackend.repository;

import hr.algebra.voyabackend.model.Reservation;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    //    Available methods:
//    findAll() — get all records
//    findById(id) — get one by ID
//    save(entity) — insert or update
//    deleteById(id) — delete
//    existsById(id) — check exists
//    count() — total records

    //  Custom methods:
    List<Reservation> findByUserId(Integer userId);
    List<Reservation> findByDriverId(Integer driverId);
    List<Reservation> findByTimeBetween(LocalDateTime from, LocalDateTime to);

}
