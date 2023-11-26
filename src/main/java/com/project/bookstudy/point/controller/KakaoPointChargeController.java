package com.project.bookstudy.point.controller;

import com.project.bookstudy.point.api.ApiResponse;
import com.project.bookstudy.point.controller.dto.KakaoPointChargePrepareResponse;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoPointChargeController {

    private final PointChargeService pointChargeService;

    @PostMapping("/api/v1/kakao/point/ready")
    public ApiResponse<KakaoPointChargePrepareResponse> getPointChargeUrl(@RequestBody PointChargePrepareRequest request) {

        PointChargePrepareServiceResponse response = pointChargeService.prepare(request.getMemberId(), request.getPointAmount());
        KakaoPointChargePrepareResponse kakaoPointChargePrepareResponse = KakaoPointChargePrepareResponse.fromServiceResponse(response);

        return ApiResponse.ok(kakaoPointChargePrepareResponse);
    }
}
