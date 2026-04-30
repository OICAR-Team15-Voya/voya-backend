package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.ReservationCreateDto;
import hr.algebra.voyabackend.model.dto.ReservationDto;
import hr.algebra.voyabackend.model.dto.ReservationUpdateDto;
import hr.algebra.voyabackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("voya/api/reservations") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Returns all reservations from the database.
     * Should be accessible only by ADMIN.
     * URL is: /voya/api/reservations/all
     * @return List<ReservationDto> 200 if OK
     */
    @GetMapping("/all")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    /**
     * Returns reservation by id.
     * Should be accessible only by ADMIN.
     * URL is: /voya/api/reservations/{id}
     * @param id reservation id
     * @return ReservationDto 200 if OK
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    /**
     * Returns reservations for a specific user.
     * Should be accessible only by ADMIN.
     * URL is: /voya/api/reservations/user/{id}
     * @param id user id
     * @return List<ReservationDto> 200 if OK
     */
    @GetMapping("/user/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDto>> getReservationsForUser(@PathVariable Integer id) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(id));
    }

    /**
     * Call this endpoint to get all reservations for a user (client).
     * This method should be called only by mobile app client
     * URL is: /voya/api/reservations/my-reservations
     * @param currentUser logged in client
     * @return List<ReservationDto> 200 if OK
     */
    @GetMapping("/my-reservations")
    //@PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ReservationDto>> getMyReservations(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(reservationService.getReservationsByEmail(currentUser.getUsername()));
    }

    /**
     * Call this endpoint to get all reservations assigned to a driver.
     * This method should be called only by web app client
     * URL is: /voya/api/reservations/my-driver-reservations
     * @param currentUser logged in driver
     * @return List<ReservationDto> 200 if OK
     */
    @GetMapping("/my-rides")
    //@PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<ReservationDto>> getAssignedRidesForDriver(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(reservationService.getAssignedRidesForDriver(currentUser.getUsername()));
    }

    /**
     * This endpoint creates a new reservation.
     * Should be accessible by ADMIN and CLIENT.
     * URL is: /voya/api/reservations
     * @param dto ReservationCreateDto
     * @return ReservationDto 201 if Created
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(dto));
    }

    /**
     * Updates a reservation. Should be accessible only by ADMIN.
     * For client updates to the reservation, call the method below.
     * Reson: the client can update only his own reservations and not all fields.
     * @param id reservation id
     * @param dto ReservationUpdateDto
     * @return ReservationDto 200 if OK
     */
    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> adminUpdateReservation(@PathVariable Integer id, @RequestBody ReservationUpdateDto dto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, dto));
    }

    @PutMapping("/my-reservations/{id}")
    //@PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReservationDto> clientUpdateReservation(@PathVariable Integer id,
                                                                  @RequestBody ReservationCreateDto dto,
                                                                  @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(reservationService.clientReservationUpdate(id, dto, currentUser.getUsername()));
    }




    /**
     * Set reservation status to IN_PROGRESS - ADMIN or DRIVER.
     * Should be accessible only by ADMIN or DRIVER in web app.
     * URL is: /voya/api/reservations/set-in-progress/{id}
     * @param id reservation id
     * @return 200 if OK
     */
    @PatchMapping("/set-in-progress/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Void> setStatusToInProgress(@PathVariable Integer id) {
        reservationService.setStatusToInProgress(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Set reservation status to COMPLETED - ADMIN or DRIVER.
     * Should be accessible only by ADMIN or DRIVER in web app.
     * URL is: /voya/api/reservations/set-completed/{id}
     * @param id reservation id
     * @return 200 if OK
     */
    @PatchMapping("/set-completed/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<Void> setStatusToCompleted(@PathVariable Integer id) {
        reservationService.setStatusToCompleted(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel reservation. Should be accessible only by CLIENT in the mobile app
     * URL is: /voya/api/reservations/cancel/{id}
     * @param id reservation id
     * @param currentUser logged in user
     * @return 200 if OK
     */
    @PatchMapping("/cancel/{id}")
    //@PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Integer id,
                                                  @AuthenticationPrincipal UserDetails currentUser) {
        reservationService.cancelReservation(id, currentUser.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Delete reservation. Should be accessible only by ADMIN.
     * @param id reservation id
     * @return 200 if OK
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok().build();
    }
}
