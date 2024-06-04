package com.github.supercodingspring.repository.payment;
import com.github.supercodingspring.web.dto.airline.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment paymentRequestDtoToPayment(PaymentDto.PaymentRequest paymentRequest);

    PaymentDto.PaymentResponse paymentToPaymentResponseDto(Payment payment);
}
