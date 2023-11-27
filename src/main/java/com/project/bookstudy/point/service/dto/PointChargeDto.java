package com.project.bookstudy.point.service.dto;

import com.project.bookstudy.point.domain.PointCharge;
import com.project.bookstudy.point.domain.PointChargeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointChargeDto {
    private Long id;
    private String transactionId;
    private Long chargeAmount;
    private LocalDateTime chargeDt;
    private PointChargeStatus status;

    @Builder
    private PointChargeDto(Long id, String transactionId, Long chargeAmount, LocalDateTime chargeDt, PointChargeStatus status) {
        this.id = id;
        this.transactionId = transactionId;
        this.chargeAmount = chargeAmount;
        this.chargeDt = chargeDt;
        this.status = status;
    }

    public static PointChargeDto fromEntity(PointCharge history) {
        return PointChargeDto.builder()
                .id(history.getId())
                .transactionId(history.getTransactionId())
                .chargeAmount(history.getChargeAmount())
                .chargeDt(history.getChargeDt())
                .status(history.getStatus())
                .build();
    }

}
