package hr.algebra.voyabackend.schedulers;

import hr.algebra.voyabackend.repository.ReservationRepository;
import hr.algebra.voyabackend.service.NotificationSenderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduler class that checks for upcoming reservations and sends reminders.
 * This scheduler runs every minute.
 * Checks if there is 2 hours before the reservation time.
 * If there is, sends a reminder email.
 */
@Component
public class ReservationReminderScheduler {

    private final ReservationRepository reservationRepository;
    private final NotificationSenderService notificationService;

    public ReservationReminderScheduler(ReservationRepository reservationRepository, NotificationSenderService notificationService) {
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void checkUpcomingReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursFromNow = now.plusHours(2);

        reservationRepository.findByTimeBetweenAndReminderSentFalse(now, twoHoursFromNow)
                .forEach(reservation -> {
                    notificationService.sendReservationReminderEmail(reservation);
                    reservation.setReminderSent(true);
                    reservationRepository.save(reservation);
                });
    }
}