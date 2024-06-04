package com.github.supercodingspring.repository.payment;

import com.github.supercodingspring.repository.passenger.Passenger;
import com.github.supercodingspring.repository.reservations.Reservation;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "pasenger_id")
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column
    private LocalDateTime reserve_at;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }

        Payment payment = (Payment) o;

        return paymentId.equals(payment.paymentId);
    }

    @Override
    public int hashCode() {
        return paymentId.hashCode();
    }
}
