package com.project.bookstudy.enrollment.controller;

import com.project.bookstudy.enrollment.dto.CreateEnrollmentResponse;
import com.project.bookstudy.enrollment.service.EnrollmentService;
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
     * @param StudyGroupId
     * @param authentication
     * 스터디 그룹 신청하기
     */
    @PostMapping
    public ResponseEntity<CreateEnrollmentResponse> createEnrollment(@RequestParam Long StudyGroupId, Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.enroll(StudyGroupId, authentication));
    }
}
