package com.project.bookstudy.point.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.point.domain.PointCharge;
import com.project.bookstudy.point.repository.PointChargeRepository;
import com.project.bookstudy.point.service.dto.PointChargePrepareServiceResponse;
import com.project.bookstudy.point.service.dto.PointSystemPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
}
