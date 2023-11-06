package com.project.bookstudy.study_group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-group")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public ResponseEntity<Void> createStudyGroup(@Valid @RequestBody StudyGroupCreateRequest request) {
        StudyGroupCreateResponse response = studyGroupService.createStudyGroup(request.toCreateServiceParam());
        return ResponseEntity.ok().build();
    }
}
