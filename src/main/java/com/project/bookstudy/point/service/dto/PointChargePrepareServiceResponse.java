package com.project.bookstudy.point.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class PointChargePrepareServiceResponse {
    private Long pointChargeId;
    private Map<String, String> extraData;

    @Builder
    public PointChargePrepareServiceResponse(Long pointChargeId, Map<String, String> extraData) {
        this.pointChargeId = pointChargeId;
        this.extraData = extraData;
    }
}
