package com.github.supercodingspring.service;

import com.github.supercodingspring.repository.airlineTicket.AirlineTicket;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketAndFlightInfo;
import com.github.supercodingspring.repository.airlineTicket.AirlineTicketRepository;
import com.github.supercodingspring.repository.passenger.Passenger;
import com.github.supercodingspring.repository.passenger.PassengerReposiotry;
import com.github.supercodingspring.repository.payment.Payment;
import com.github.supercodingspring.repository.payment.PaymentRepository;
import com.github.supercodingspring.repository.reservations.Reservation;
import com.github.supercodingspring.repository.reservations.ReservationRepository;
import com.github.supercodingspring.repository.users.UserEntity;
import com.github.supercodingspring.repository.users.UserRepository;
import com.github.supercodingspring.web.dto.airline.ReservationRequest;
import com.github.supercodingspring.web.dto.airline.ReservationResult;
import com.github.supercodingspring.web.dto.airline.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AirReservationService {

    private UserRepository userRepository;
    private AirlineTicketRepository airlineTicketRepository;

    private PassengerReposiotry passengerReposiotry;
    private ReservationRepository reservationRepository;

    private PaymentRepository paymentRepository;

    public List<Ticket> findUserFavoritePlaceTickets(Integer userId, String ticketType) {
        // 1. 유저를 userId 로 가져와서, 선호하는 여행지 도출
        // 2. 선호하는 여행지와 ticketType으로 AirlineTIcket table 질의 해서 필요한 AirlineTicket
        // 3. 이 둘의 정보를 조합해서 Ticket DTO를 만든다.
        UserEntity userEntity = userRepository.findUserById(userId);
        String likePlace = userEntity.getLikeTravelPlace();

        List<AirlineTicket> airlineTickets
                = airlineTicketRepository.findAllAirlineTicketsWithPlaceAndTicketType(likePlace, ticketType);

        List<Ticket> tickets = airlineTickets.stream().map(Ticket::new).collect(Collectors.toList());
        return tickets;
    }

    @Transactional(transactionManager = "tm2")
    public ReservationResult makeReservation(ReservationRequest reservationRequest) {
        // 1. Reservation Repository, Passenger Repository, Join table ( flight/airline_ticket ),

        // 0. userId,airline_ticke_id
        Integer userId = reservationRequest.getUserId();
        Integer airlineTicketId= reservationRequest.getAirlineTicketId();

        // 1. Passenger I
        Passenger passenger = passengerReposiotry.findPassengerByUserId(userId);
        Integer passengerId= passenger.getPassengerId();

        // 2. price 등의 정보 불러오기
        List<AirlineTicketAndFlightInfo> airlineTicketAndFlightInfos
                = airlineTicketRepository.findAllAirLineTicketAndFlightInfo(airlineTicketId);

        // 3. reservation 생성
        Reservation reservation = new Reservation(passengerId, airlineTicketId, rs.getInt("airline_ticket_id"), rs.getTimestamp("reserve_at").toLocalDateTime());
        Boolean isSuccess = reservationRepository.saveReservation(reservation);

        // TODO: ReservationResult DTO 만들기
        List<Integer> prices = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getPrice).collect(Collectors.toList());
        List<Integer> charges = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getCharge).collect(Collectors.toList());
        Integer tax = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getTax).findFirst().get();
        Integer totalPrice = airlineTicketAndFlightInfos.stream().map(AirlineTicketAndFlightInfo::getTotalPrice).findFirst().get();

        return new ReservationResult(prices, charges, tax, totalPrice, isSuccess);
    }

    @Transactional(transactionManager = "tm2")
    public int makePayment(List<Integer> user_ids, List<Integer> reservation_ids) {
        List<Passenger> passengers = new ArrayList<>();
        user_ids.forEach(userId -> {
            Passenger passenger = passengerReposiotry.findPassengerByUserId(userId);
            passengers.add(passenger);
        });

        List<Reservation> reservations = new ArrayList<>();
        reservation_ids.forEach(reservationId -> {
            Reservation reservation = reservationRepository.findReservationById(reservationId);
            reservations.add(reservation);
        });

        // TODO. 결제가 진행된 예약은 기존 “대기” 상태에서 “확정” 상태로 바뀌어야합니다.

        for(int i=0; i<passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            Reservation reservation = reservations.get(i);
            Payment payment = Payment.builder()
                    .reserve_at(reservation.getReserveAt())
                    .passenger(passenger)
                    .reservation(reservation)
                    .build();
            paymentRepository.save(payment);
        }

        return reservation_ids.size();
    }
}
