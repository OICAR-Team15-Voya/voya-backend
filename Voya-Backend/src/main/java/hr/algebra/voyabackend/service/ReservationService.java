package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.*;
import hr.algebra.voyabackend.model.dto.ReservationCreateDto;
import hr.algebra.voyabackend.model.dto.ReservationDto;
import hr.algebra.voyabackend.model.dto.ReservationUpdateDto;
import hr.algebra.voyabackend.model.enums.Status;
import hr.algebra.voyabackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final NotificationSenderService notificationSenderService;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, DriverRepository driverRepository, VehicleRepository vehicleRepository, VehicleCategoryRepository vehicleCategoryRepository, NotificationSenderService notificationSenderService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleCategoryRepository = vehicleCategoryRepository;
        this.notificationSenderService = notificationSenderService;
    }

    /**
     * Get all reservations. This method is only for ADMIN.
     * @return List of ReservationDto
     */
    public List<ReservationDto> getAll() {
        return reservationRepository
                .findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Get reservation by id. This method is only for ADMIN.
     * @param id reservation id
     * @return ReservationDto
     */
    public ReservationDto getReservationById(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
        return mapToDto(reservation);
    }

    /**
     * Get all reservations for a specific user (client)
     * @param userId user id
     * @return List of ReservationDto containing user id
     */
    public List<ReservationDto> getReservationsByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * This method serves the getMyReservations endpoint.
     * It returns all reservations for a specific user (client) - authenticated by JWT token.
     * @param email user email
     * @return List of ReservationDto
     */
    public List<ReservationDto> getReservationsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return reservationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Get driver assigned rides.
     * @param email driver email
     * @return List of ReservationDto
     */
    public List<ReservationDto> getAssignedRidesForDriver(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        return reservationRepository.findByDriverId(driver.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Method used by the client to update their own reservation.
     * @param id reservation id
     * @param dto ReservationCreateDto
     * @param email user email
     * @return ReservationDto
     */
    public ReservationDto clientReservationUpdate (Integer id, ReservationCreateDto dto, String email) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You can only update your own reservations");
        }

        if (reservation.getStatus() == Status.CANCELLED || reservation.getStatus() == Status.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot update a cancelled or completed reservation");
        }

        VehicleCategory vehicleCategory = vehicleCategoryRepository.findById(dto.getVehicleCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found"));

        reservation.setVehicleCategory(vehicleCategory);
        reservation.setTime(dto.getTime());
        reservation.setPickupLocation(dto.getPickupLocation());
        reservation.setDropoffLocation(dto.getDropoffLocation());
        reservation.setPassengerNumber(dto.getPassengerNumber());
        reservation.setLuggageNumber(dto.getLuggageNumber());
        reservation.setWelcomeSign(dto.getWelcomeSign());
        reservation.setAdditionalNotes(dto.getAdditionalNotes());

        return mapToDto(reservationRepository.save(reservation));
    }

    /**
     * Create a new reservation
     * This method should be accessible to both ADMIN and USER
     * @param dto ReservationCreateDto
     * @return ReservationDto
     */
    public ReservationDto createReservation(ReservationCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + dto.getUserId()));

        VehicleCategory vehicleCategory = vehicleCategoryRepository.findById(dto.getVehicleCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + dto.getVehicleCategoryId()));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setVehicleCategory(vehicleCategory);
        reservation.setTime(dto.getTime());
        reservation.setPickupLocation(dto.getPickupLocation());
        reservation.setDropoffLocation(dto.getDropoffLocation());
        reservation.setPassengerNumber(dto.getPassengerNumber());
        reservation.setLuggageNumber(dto.getLuggageNumber());
        reservation.setWelcomeSign(dto.getWelcomeSign());
        reservation.setAdditionalNotes(dto.getAdditionalNotes());
        reservation.setStatus(Status.CONFIRMED);
        reservation.setIsPaid(false);

        Reservation newReservation = reservationRepository.save(reservation);
        notificationSenderService.sendNewReservationEmail(newReservation);
        return mapToDto(newReservation);
    }

    /**
     * Call this method to update a reservation.
     * This method should be accessible to both ADMIN and USER
     * @param id reservation id
     * @param dto ReservationUpdateDto - the new information
     * @return ReservationDto
     */
    public ReservationDto updateReservation(Integer id, ReservationUpdateDto dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));

        VehicleCategory vehicleCategory = vehicleCategoryRepository.findById(dto.getVehicleCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found with id: " + dto.getVehicleCategoryId()));

        // check if a driver is assigned
        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist"));
            reservation.setDriver(driver);
        }

        // check if a vehicle is assigned
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle does not exist"));
            reservation.setVehicle(vehicle);
        }

        reservation.setVehicleCategory(vehicleCategory);
        reservation.setTime(dto.getTime());
        reservation.setPickupLocation(dto.getPickupLocation());
        reservation.setDropoffLocation(dto.getDropoffLocation());
        reservation.setPassengerNumber(dto.getPassengerNumber());
        reservation.setLuggageNumber(dto.getLuggageNumber());
        reservation.setWelcomeSign(dto.getWelcomeSign());
        reservation.setAdditionalNotes(dto.getAdditionalNotes());
        reservation.setStatus(Status.valueOf(dto.getStatus()));
        reservation.setPrice(dto.getPrice());
        reservation.setIsPaid(dto.getIsPaid() != null ? dto.getIsPaid() : reservation.getIsPaid());

        return mapToDto(reservationRepository.save(reservation));
    }

    /**
     * Set the reservation status to IN_PROGRESS.
     * This method should be accessible to both DRIVER and ADMIN
     * @param id reservation id
     */
    public void setStatusToInProgress(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
        reservation.setStatus(Status.IN_PROGRESS);
        reservationRepository.save(reservation);
    }

    /**
     * Set the reservation status to COMPLETED.
     * This method should be accessible to both DRIVER and ADMIN
     * @param id reservation id
     */
    public void setStatusToCompleted(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
        reservation.setStatus(Status.COMPLETED);
        reservationRepository.save(reservation);
    }

    /**
     * Client can cancel their own reservation. Sets the status to CANCELLED.
     * @param id reservation id
     * @param currentUserEmail email of the authenticated user
     */
    public void cancelReservation(Integer id, String currentUserEmail) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));

        if (!reservation.getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You can only cancel your own reservations");
        }

        if (reservation.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is already cancelled");
        }

        reservation.setStatus(Status.CANCELLED);
        reservationRepository.save(reservation);
    }

    /**
     * Admin may delete a reservation permanently
     * @param id reservation id
     */
    public void deleteReservation(Integer id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    // MAPPER
    private ReservationDto mapToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserFirstName(reservation.getUser().getFirstName());
        dto.setUserLastName(reservation.getUser().getLastName());
        dto.setUserEmail(reservation.getUser().getEmail());
        dto.setVehicleCategoryId(reservation.getVehicleCategory().getId());
        dto.setVehicleCategoryName(reservation.getVehicleCategory().getName());
        dto.setTime(reservation.getTime());
        dto.setPickupLocation(reservation.getPickupLocation());
        dto.setDropoffLocation(reservation.getDropoffLocation());
        dto.setPassengerNumber(reservation.getPassengerNumber());
        dto.setLuggageNumber(reservation.getLuggageNumber());
        dto.setWelcomeSign(reservation.getWelcomeSign());
        dto.setAdditionalNotes(reservation.getAdditionalNotes());
        dto.setStatus(reservation.getStatus().name());
        dto.setPrice(reservation.getPrice());
        dto.setIsPaid(reservation.getIsPaid());

        if (reservation.getDriver() != null) {
            dto.setDriverId(reservation.getDriver().getId());
            dto.setDriverFirstName(reservation.getDriver().getUser().getFirstName());
            dto.setDriverLastName(reservation.getDriver().getUser().getLastName());
        }

        if (reservation.getVehicle() != null) {
            dto.setVehicleId(reservation.getVehicle().getId());
            dto.setVehicleName(reservation.getVehicle().getName());
            dto.setVehicleRegistration(reservation.getVehicle().getRegistration());
        }
        return dto;
    }
}
