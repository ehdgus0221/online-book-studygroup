package com.project.bookstudy.study_group.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class UpdateStudyGroupRequest {
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
    public UpdateStudyGroupRequest(int maxSize, String contents, String subject, Long price, String contentsDetail, LocalDateTime studyStartDt, LocalDateTime studyEndDt, LocalDateTime recruitmentEndDt, LocalDateTime recruitmentStartDt) {

        this.maxSize = maxSize;
        this.contents = contents;
        this.subject = subject;
        this.price = price;
        this.contentsDetail = contentsDetail;
        this.studyStartDt = studyStartDt;
        this.studyEndDt = studyEndDt;
        this.recruitmentEndDt = recruitmentEndDt;
        this.recruitmentStartDt = recruitmentStartDt;
    }

    public UpdateStudyGroupParam toUpdateStudyGroupParam(Long studyGroupId) {
        return UpdateStudyGroupParam.builder()
                .id(studyGroupId)
                .maxSize(maxSize)
                .contents(contents)
                .subject(subject)
                .price(price)
                .contentsDetail(contentsDetail)
                .studyStartDt(studyStartDt)
                .studyEndDt(studyEndDt)
                .recruitmentStartDt(recruitmentStartDt)
                .recruitmentEndDt(recruitmentEndDt)
                .build();
    }

}
