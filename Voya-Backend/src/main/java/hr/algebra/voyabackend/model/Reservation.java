package hr.algebra.voyabackend.model;

import hr.algebra.voyabackend.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@AllArgsConstructor @NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_category_id", nullable = false)
    @ToString.Exclude
    private VehicleCategory vehicleCategory;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(name = "pickup_location", nullable = false, length = 255)
    private String pickupLocation;

    @Column(name = "dropoff_location", nullable = false, length = 255)
    private String dropoffLocation;

    @Column(name = "passenger_number")
    private Integer passengerNumber;

    @Column(name = "luggage_number")
    private Integer luggageNumber;

    @Column(name = "welcome_sign", length = 100)
    private String welcomeSign;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @ToString.Exclude
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @ToString.Exclude
    private Vehicle vehicle;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

}