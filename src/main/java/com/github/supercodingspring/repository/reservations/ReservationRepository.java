package com.github.supercodingspring.repository.reservations;

import com.github.supercodingspring.repository.passenger.Passenger;

public interface ReservationRepository {
    Boolean saveReservation(Reservation reservation);
    Reservation findReservationById(Integer reservationId);
}
