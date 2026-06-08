package hr.algebra.voyabackend.repository;

import hr.algebra.voyabackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH r.vehicle v " +
            "LEFT JOIN FETCH v.vehicleCategory " +
            "LEFT JOIN FETCH r.vehicleCategory")
    List<Reservation> findAllWithDetails();

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH r.vehicle v " +
            "LEFT JOIN FETCH v.vehicleCategory " +
            "LEFT JOIN FETCH r.vehicleCategory " +
            "WHERE r.user.id = :userId")
    List<Reservation> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH r.vehicle v " +
            "LEFT JOIN FETCH v.vehicleCategory " +
            "LEFT JOIN FETCH r.vehicleCategory " +
            "WHERE r.driver.id = :driverId")
    List<Reservation> findByDriverId(@Param("driverId") Integer driverId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH r.vehicle v " +
            "LEFT JOIN FETCH v.vehicleCategory " +
            "LEFT JOIN FETCH r.vehicleCategory " +
            "WHERE r.time BETWEEN :from AND :to")
    List<Reservation> findByTimeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH r.vehicle v " +
            "LEFT JOIN FETCH v.vehicleCategory " +
            "LEFT JOIN FETCH r.vehicleCategory " +
            "WHERE r.time BETWEEN :from AND :to AND r.reminderSent = false")
    List<Reservation> findByTimeBetweenAndReminderSentFalse(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
