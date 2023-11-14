package com.project.bookstudy.study_group.controller;

import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-group")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    /**
     *
     * @param authentication
     * @param request
     * 스터디 그룹 생성
     */
    @PostMapping
    public CreateStudyGroupResponse createStudyGroup(Authentication authentication,
                                                     @Valid @RequestBody CreateStudyGroupRequest request) {
        StudyGroupDto studyGroupDto = studyGroupService
                .createStudyGroup(authentication, request.toStudyGroupParam());
        return CreateStudyGroupResponse.builder()
                .studyGroupId(studyGroupDto.getId())
                .leaderId(studyGroupDto.getLeaderId())
                .build();
    }

    /**
     *
     * @param pageable
     * @param cond
     * 스터디그룹 전체 조회
     * StudyGroupSearchCond를 통해 2가지 검색 기능을 지원한다.
     * 1. StudyGroup을 만든 사람 이름
     * 2. StudyGroup 제목
     */
    @GetMapping
    public Page<StudyGroupDto> getStudyGroupList (@PageableDefault Pageable pageable,
                                                  @ModelAttribute StudyGroupSearchCond cond) {
        return studyGroupService.getStudyGroupList(pageable, cond);
    }

    /**
     *
     * @param studyGroupId
     * 특정 스터디그룹 조회
     */
    @GetMapping("/{id}")
    public StudyGroupDto getStudyGroup (@PathVariable("id") Long studyGroupId) {
        return studyGroupService.getStudyGroup(studyGroupId);
    }

    /**
     *
     * @param studyGroupId
     * @param request
     * 스터디그룹 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStudyGroup (@PathVariable("id") Long studyGroupId
            , @RequestBody UpdateStudyGroupRequest request
    , Authentication authentication) {
        studyGroupService.updateStudyGroup(request.toUpdateStudyGroupParam(studyGroupId), authentication);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable("id") Long studyGroupId
            , Authentication authentication) {
        studyGroupService.deleteStudyGroup(studyGroupId, authentication);
        return ResponseEntity.ok().build();
    }

}
