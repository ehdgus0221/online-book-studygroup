package com.project.bookstudy.point.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointCharge {

    @Id
    @GeneratedValue
    private Long id;
    private String tempKey;
    private String transactionId;
    private Long chargeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime chargeDt;
    private PointChargeStatus status;

    @Builder
    private PointCharge(String tempKey, String transactionId, Long chargeAmount, Member member, LocalDateTime chargeDt) {
        this.tempKey = tempKey;
        this.transactionId = transactionId;
        this.chargeAmount = chargeAmount;
        this.member = member;
        this.chargeDt = chargeDt;

        this.status = PointChargeStatus.IN_PROGRESS;
    }

    public void success(LocalDateTime chargeDt) {
        member.chargePoint(chargeAmount);
        this.status = PointChargeStatus.SUCCESS;
        this.chargeDt = chargeDt;
    }

    public void terminateIn(PointChargeStatus status) {
        this.status = status;
    }
}
