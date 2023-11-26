package com.project.bookstudy.point.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.point.domain.PointCharge;
import com.project.bookstudy.point.domain.PointChargeStatus;
import com.project.bookstudy.point.repository.PointChargeRepository;
import com.project.bookstudy.point.service.dto.PointChargeDto;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import com.project.bookstudy.point.service.dto.PointSystemApprovalResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.project.bookstudy.point.domain.PointChargeStatus.FAIL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointChargeService {

    private final PointClient pointClient;
    private final MemberRepository memberRepository;
    private final PointChargeRepository pointChargeRepository;

    @Transactional
    public PointChargePrepareServiceResponse prepare(Long memberId, Long chargeAmount) {

        //setting
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        //logic
        String tempKey = UUID.randomUUID().toString();  //redirect url 설정 시 같이 전달할 정보,

        //RestTemplate 사용 > Blocking 걸린다. 그러면, Transaction을 Service에서 걸어야 할까?
        //내 생각) Service에 걸어야 한다. >> 원자성 유지 (현재 로직에서 문제는 없지만, 앞으로를 위해 걸어둔다)
        PointSystemPreparationResponse response = pointClient.preparePointCharge(MemberDto.fromEntity(member), chargeAmount, tempKey);

        PointCharge pointCharge = PointCharge.builder()
                .tempKey(tempKey)
                .transactionId(response.getTransactionId())
                .chargeAmount(chargeAmount)
                .member(member)
                .build();

        pointChargeRepository.save(pointCharge);

        return PointChargePrepareServiceResponse.builder()
                .pointChargeId(pointCharge.getId())
                .extraData(response.getExtraData())
                .build();
    }

    @Transactional
    public void terminate(String tempKey, PointChargeStatus status) {

        //setting
        PointCharge pointCharge = pointChargeRepository.findByTempKey(tempKey)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POINT_CHARGE_NOT_FOUND.getDescription()));

        //logic
        pointCharge.terminateIn(status);
    }

    @Transactional
    public PointChargeDto approve(String tempKey, Map<String, String> extraRequestData) {

        //setting
        PointCharge pointCharge = pointChargeRepository.findByTempKey(tempKey)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POINT_CHARGE_NOT_FOUND.getDescription()));

        Member member = pointCharge.getMember();

        //logic
        PointSystemApprovalResponse response = pointClient.approvePointCharge(MemberDto.fromEntity(member), PointChargeDto.fromEntity(pointCharge), extraRequestData);
        validate(response, pointCharge);
        pointCharge.success(response.getApprovedAt());

        return PointChargeDto.fromEntity(pointCharge);
    }

    private void validate(PointSystemApprovalResponse response, PointCharge pointCharge) {
        if (!response.getTransactionId().equals(pointCharge.getTransactionId())
                || pointCharge.getChargeAmount().longValue() != response.getTotalAmount().longValue()) {
            pointCharge.terminateIn(FAIL);
            throw new IllegalStateException(ErrorCode.CHARGE_FAILED.getDescription());
        }
    }
}
