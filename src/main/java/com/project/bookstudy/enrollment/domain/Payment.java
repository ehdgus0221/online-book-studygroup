package com.project.bookstudy.enrollment.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long id;

    private LocalDateTime paymentDt;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Long price;
    private Long discountPrice;
    private Long paymentPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
