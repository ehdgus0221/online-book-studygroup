package com.project.bookstudy.enrollment.controller;

import com.project.bookstudy.enrollment.dto.CreateEnrollmentResponse;
import com.project.bookstudy.enrollment.service.EnrollmentService;
import com.project.bookstudy.study_group.dto.EnrollmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;


    /**
     *
     * @param studyGroupId
     * @param authentication
     * 스터디 그룹 신청하기
     */
    @PostMapping("/{studyGroupId}")
    public ResponseEntity<Long> createEnrollment(@PathVariable Long studyGroupId, Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.enroll(studyGroupId,authentication));
    }

    /**
     * @param enrollmentId
     * 스터디 그룹 취소하기
     */
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable("enrollmentId") Long enrollmentId, Authentication authentication) {
        enrollmentService.cancel(enrollmentId, authentication);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param enrollmentId
     * 스터디 그룹 신청내역 단일조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDto> getEnrollment(@PathVariable("id") Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollment(enrollmentId));
    }
}
