package com.project.bookstudy.point.controller.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class PointChargePrepareRequest {

    @NotNull(message = "회원 ID는 필수 입니다.")
    private Long memberId;

    @NotNull(message = "충전 금액은 필수 입니다.")
    @Min(value = 10_000L, message = "포인트 최소 충전 단위는 10,000원 입니다.")
    private Long pointAmount;
}
