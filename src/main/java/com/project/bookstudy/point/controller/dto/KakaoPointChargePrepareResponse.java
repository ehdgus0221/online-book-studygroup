package com.project.bookstudy.point.controller.dto;

import com.project.bookstudy.point.infra.KakaoPointClient;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoPointChargePrepareResponse {

    private static final String REDIRECT_URL_KEY = "redirectUrl";
    private static final String PG_TOKEN_KEY = "pgToken";


    private Long historyId;
    private String redirectUrl;

    @Builder
    private KakaoPointChargePrepareResponse(Long historyId, String redirectUrl) {
        this.historyId = historyId;
        this.redirectUrl = redirectUrl;
    }

    public static KakaoPointChargePrepareResponse fromServiceResponse(PointChargePrepareServiceResponse response) {
        return KakaoPointChargePrepareResponse.builder()
                .historyId(response.getPointChargeId())
                .redirectUrl(response.getExtraData().get(KakaoPointClient.REDIRECT_URL_KEY))
                .build();
    }
}
