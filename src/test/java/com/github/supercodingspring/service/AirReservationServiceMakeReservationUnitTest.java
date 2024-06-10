package com.github.supercodingspring.service;

import com.github.supercodingspring.repository.airlineTicket.AirlineTicket;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketJpaRepository;
import com.github.supercodingspring.repository.flight.Flight;
import com.github.supercodingspring.repository.passenger.Passenger;
import com.github.supercodingspring.repository.passenger.PassengerJpaRepository;
import com.github.supercodingspring.repository.reservations.ReservationJpaRepository;
import com.github.supercodingspring.repository.users.UserEntity;
import com.github.supercodingspring.service.exceptions.NotFoundException;
import com.github.supercodingspring.web.dto.airline.ReservationRequest;
import com.github.supercodingspring.web.dto.airline.ReservationResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@Slf4j
class AirReservationServiceMakeReservationUnitTest {

    @Mock
    private AirlineTicketJpaRepository airlineTicketJpaRepository;
    @Mock
    private PassengerJpaRepository passengerJpaRepository;
    @Mock
    private ReservationJpaRepository reservationJpaRepository;
    @InjectMocks
    private AirReservationService airReservationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @DisplayName("정상적으로 MakeReservation 동작하는 경우")
    @Test
    void MakeReservation() {
        // given
        Integer userId = 1;
        Integer airlineTicketId = 1;
        ReservationRequest reservationRequest = new ReservationRequest(userId, airlineTicketId);

        AirlineTicket airlineTicket = AirlineTicket.builder()
                .ticketType("편도")
                .arrivalLocation("일본")
                .departureLocation("제주도")
                .departureAt(LocalDateTime.now())
                .returnAt(LocalDateTime.now())
                .ticketId(airlineTicketId)
                .tax(100.0)
                .totalPrice(200.0)
                .build();

        List<Flight> flightList = Arrays.asList(
                new Flight(1, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "제주도", "일본", 200.0, 300.0),
                new Flight(2, airlineTicket, LocalDateTime.now(), LocalDateTime.now(), "제주도", "일본", 200.0, 300.0)
        );

        airlineTicket.setFlightList(flightList);


        Passenger passenger = Passenger.builder()
                .passengerId(2)
                .passportNum("2")
                .user(new UserEntity())
                .build();

        // when
        when(airlineTicketJpaRepository.findById(anyInt())).thenReturn(Optional.ofNullable(airlineTicket));
        when(passengerJpaRepository.findPassengerByUserUserId(userId)).thenReturn(Optional.ofNullable(passenger));
        when(reservationJpaRepository.save(any())).thenReturn(null);

        // then
        ReservationResult reservation = airReservationService.makeReservation(reservationRequest);
        System.out.println("tickets: " + reservation);
    }
}