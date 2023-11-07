package com.project.bookstudy.study_group.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class CreateStudyGroupRequest {

    private Long memberId;
    @NotBlank(message = "validation.subject.required")
    private String subject;

    @NotBlank(message = "validation.contents.required")
    private String contents;


    @Lob
    @NotBlank(message = "validation.contentsDetail.required")
    private String contentsDetail;

    @NotNull(message = "validation.maxSize.required")
    private int maxSize;

    @NotNull(message = "validation.price.required")
    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyStartDt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime studyEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentStartDt;

    @Builder
    private CreateStudyGroupRequest(Long memberId, String subject, String contents, String contentsDetail, int maxSize, Long price, LocalDateTime studyStartDt, LocalDateTime studyEndDt, LocalDateTime recruitmentStartDt, LocalDateTime recruitmentEndDt) {

        this.memberId = memberId;
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.studyStartDt = studyStartDt;
        this.studyEndDt = studyEndDt;
        this.recruitmentStartDt = recruitmentStartDt;
        this.recruitmentEndDt = recruitmentEndDt;
    }

    public CreateStudyGroupParam toStudyGroupParam() {

        return CreateStudyGroupParam.builder()

                .contentsDetail(this.contentsDetail)
                .subject(this.subject)
                .contents(this.contents)
                .maxSize(this.maxSize)
                .price(this.price)
                .studyStartDt(this.studyStartDt)
                .studyEndDt(this.studyEndDt)
                .recruitmentStartDt(this.recruitmentStartDt)
                .recruitmentEndDt(this.recruitmentEndDt)
                .build();
    }

}

