package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.param.CreateStudyGroupParam;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public StudyGroupDto createStudyGroup(Long memberId, CreateStudyGroupParam studyGroupParam) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        StudyGroup savedStudyGroup = studyGroupRepository.save(StudyGroup.from(member, studyGroupParam));

        return StudyGroupDto.fromEntity(savedStudyGroup);
    }
}