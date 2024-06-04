package com.github.supercodingspring.web.dto.airline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentDto {
    @AllArgsConstructor
    @Getter
    @Setter
    public class PaymentRequest {
        private List<Integer> user_ids;
        private List<Integer> reservation_ids;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class PaymentResponse {
        private Integer paymentId;
        private Integer passengerId;
        private Integer reservationId;
        private LocalDateTime reserveAt;
    }
}
