package com.github.supercodingspring.web.controller;

import com.github.supercodingspring.repository.payment.Payment;
import com.github.supercodingspring.repository.payment.PaymentMapper;
import com.github.supercodingspring.service.AirReservationService;
import com.github.supercodingspring.web.dto.airline.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/air-reservation")
public class AirReservationController {

    private AirReservationService airReservationService;
    private PaymentMapper paymentMapper;

    public AirReservationController(AirReservationService airReservationService) {
        this.airReservationService = airReservationService;
    }

    @GetMapping("/tickets")
    public TicketResponse findAirlineTickets(@RequestParam("user-Id") Integer userId,
                                             @RequestParam("airline-ticket-type") String ticketType ){
        List<Ticket> tickets = airReservationService.findUserFavoritePlaceTickets(userId, ticketType);
        return new TicketResponse(tickets);
    }
    @PostMapping("/reservations")
    public ReservationResult makeReservation(@RequestBody ReservationRequest reservationRequest){
        return airReservationService.makeReservation(reservationRequest);
    }

    @PostMapping("/payments")
    public ResponseEntity makePayment(@RequestBody PaymentDto.PaymentRequest paymentRequest) {
        int payment_size = airReservationService.makePayment(
                paymentRequest.getUser_ids(), paymentRequest.getReservation_ids()
        );
//        PaymentDto.PaymentResponse response = paymentMapper.paymentToPaymentResponseDto(payment);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
        return new ResponseEntity<>("요청한 결제 중 " + payment_size + "건 진행완료 되었습니다.", HttpStatus.CREATED);
    }
}
