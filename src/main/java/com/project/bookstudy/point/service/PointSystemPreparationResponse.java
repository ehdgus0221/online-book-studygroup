package com.project.bookstudy.point.service;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class PointSystemPreparationResponse {
    private String transactionId;
    private Map<String, String> extraData;  //특정 결제 시스템에 종속되지 않도록 함

    @Builder
    public PointSystemPreparationResponse(String transactionId, Map<String, String> extraData) {
        this.transactionId = transactionId;
        this.extraData = extraData;
    }
}
