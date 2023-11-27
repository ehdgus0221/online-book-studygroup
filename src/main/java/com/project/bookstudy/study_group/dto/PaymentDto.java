package com.project.bookstudy.study_group.dto;

import com.project.bookstudy.enrollment.domain.Payment;
import com.project.bookstudy.enrollment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentDto {

    private Long id;

    private LocalDateTime paymentDate;
    private PaymentStatus status;

    private Long price;
    private Long discountPrice;
    private Long paymentPrice;

    @Builder
    private PaymentDto(Long id, LocalDateTime paymentDate, PaymentStatus status, Long price, Long discountPrice, Long paymentPrice) {

        this.id = id;
        this.paymentDate = paymentDate;
        this.status = status;
        this.price = price;
        this.discountPrice = discountPrice;
        this.paymentPrice = paymentPrice;
    }

    public static PaymentDto fromEntity(Payment payment) {

        return PaymentDto.builder()
                .id(payment.getId())
                .paymentDate(payment.getPaymentDt())
                .status(payment.getStatus())
                .price(payment.getPrice())
                .discountPrice(payment.getDiscountPrice())
                .paymentPrice(payment.getPaymentPrice())
                .build();
    }
}
