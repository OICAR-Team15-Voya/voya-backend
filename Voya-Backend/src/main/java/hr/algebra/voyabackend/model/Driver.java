package hr.algebra.voyabackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "drivers")
@Data
@AllArgsConstructor @NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    private User user;

    @Column(name = "license_valid_until", nullable = false)
    private LocalDate licenseValidUntil;

    @OneToMany(mappedBy = "driver")
    private List<Reservation> reservations;
}