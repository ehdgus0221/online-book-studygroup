package com.project.bookstudy.study_group.service;

import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class StudyGroupQuartzScheduler {
    /**
     * 스케쥴러 Cron
     * 스터디 모집 기간, 스터디 진행 기간에 따른 스터디그룹 상태 업데이트
     * 매일 자정 (0시)에 진행
     */
    private final StudyGroupRepository studyGroupRepository;

    @Scheduled(cron = "* * 0 * * *")
    @Transactional
    public void checkStudyGroupStatus() {
        List<StudyGroup> studyGroupList = studyGroupRepository.findAll();

        for (StudyGroup studygroup : studyGroupList) {
            studygroup.updateStatus();
        }
    }
}
