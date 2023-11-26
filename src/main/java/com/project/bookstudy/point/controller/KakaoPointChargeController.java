package com.project.bookstudy.point.controller;

import com.project.bookstudy.point.api.ApiResponse;
import com.project.bookstudy.point.controller.dto.KakaoPointChargePrepareResponse;
import com.project.bookstudy.point.controller.dto.PointChargePrepareRequest;
import com.project.bookstudy.point.domain.PointChargeStatus;
import com.project.bookstudy.point.infra.KakaoPointClient;
import com.project.bookstudy.point.service.PointChargeService;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakao/point")
public class KakaoPointChargeController {

    private final PointChargeService pointChargeService;

    @PostMapping("/ready")
    public ApiResponse<KakaoPointChargePrepareResponse> getPointChargeUrl(@RequestBody PointChargePrepareRequest request) {

        PointChargePrepareServiceResponse response = pointChargeService.prepare(request.getMemberId(), request.getPointAmount());
        KakaoPointChargePrepareResponse kakaoPointChargePrepareResponse = KakaoPointChargePrepareResponse.fromServiceResponse(response);

        return ApiResponse.ok(kakaoPointChargePrepareResponse);
    }

    @RequestMapping("/cancel")
    public ApiResponse<Void> cancelPointCharge(@RequestParam("temp_key") String tempKey) {
        pointChargeService.terminate(tempKey, PointChargeStatus.CANCEL);
        return ApiResponse.of(HttpStatus.ACCEPTED, "결제가 취소되었습니다.", null);
    }

    @GetMapping("/fail")
    public ApiResponse<Void> failPointCharge(@RequestParam("temp_key") String tempKey) {
        pointChargeService.terminate(tempKey, PointChargeStatus.FAIL);
        return ApiResponse.of(HttpStatus.SERVICE_UNAVAILABLE, "결제가 실패했습니다.", null);
    }

    @GetMapping("/approval/success")
    public ApiResponse<PointChargeDto> approvePointCharge(@RequestParam("temp_key") String tempKey, @RequestParam("pg_token") String token) {
        Map<String, String> extraRequestData = Map.of(KakaoPointClient.PG_TOKEN_KEY, token);
        PointChargeDto pointChargeDto = pointChargeService.approve(tempKey, extraRequestData);

        return ApiResponse.ok(pointChargeDto);
    }
}

