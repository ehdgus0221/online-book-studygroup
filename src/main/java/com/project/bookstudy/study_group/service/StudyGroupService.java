package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.domain.param.UpdateStudyGroupParam;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public StudyGroupDto createStudyGroup(Authentication authentication, CreateStudyGroupParam studyGroupParam) {

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        StudyGroup savedStudyGroup = studyGroupRepository.save(StudyGroup.from(member, studyGroupParam));

        return StudyGroupDto.fromEntity(savedStudyGroup);
    }

    public Page<StudyGroupDto> getStudyGroupList(Pageable pageable, StudyGroupSearchCond cond) {

        Page<StudyGroup> studyGroups = studyGroupRepository.searchStudyGroup(pageable, cond);

        return studyGroups.map(entity -> StudyGroupDto.fromEntity(entity));
    }

    public StudyGroupDto getStudyGroup(Long studyGroupId) {

        StudyGroup studyGroup = studyGroupRepository.findByIdWithLeader(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription()));

        return StudyGroupDto.fromEntity(studyGroup);
    }

    @Transactional
    public void updateStudyGroup(UpdateStudyGroupParam updateParam) {

        StudyGroup studyGroup = studyGroupRepository.findById(updateParam.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription()));

        studyGroup.update(updateParam);
    }
}
