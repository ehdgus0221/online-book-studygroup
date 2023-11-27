package com.project.bookstudy.point.service;

import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointSystemApprovalResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;

import java.util.Map;

public interface PointClient {
    PointSystemPreparationResponse preparePointCharge(MemberDto memberDto, Long pointAmount, String tempKey);

    PointSystemApprovalResponse approvePointCharge(MemberDto memberDto, PointChargeDto history, Map<String, String> extraRequestData);
}
