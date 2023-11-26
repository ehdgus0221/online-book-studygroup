package com.project.bookstudy.point.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointSystemApprovalResponse {
    private Long totalAmount;
    private String transactionId;
    private LocalDateTime approvedAt;

    @Builder
    private PointSystemApprovalResponse(Long totalAmount, String transactionId, LocalDateTime approvedAt) {
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
        this.approvedAt = approvedAt;
    }
}
