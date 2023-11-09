package com.project.bookstudy.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS("이메일이 이미 존재합니다."),
    VALIDATION_ERROR("유효성 검사 에러"),
    TOKEN_INVALID("유효하지 않은 토큰입니다."),
    EXPIRATION_TOKEN("만료된 토큰입니다."),
    INCORRECT_APPROACH("올바르지 않은 접근입니다."),
    TOKEN_NOT_FOUND("DB에 존재하지 않는 Refresh 토큰입니다."),
    AUTHORIZATION_NOT_FOUND("권한이 존재하지 않습니다."),
    LOGIN_REQUIRED("로그인이 필요합니다"),
    LOGIN_FAILED("로그인에 실패하였습니다."),
    SUBJECT_ALREADY_EXISTS("동일한 제목이 이미 존재합니다."),
    STUDY_GROUP_NOT_FOUND("해당 스터디 그룹이 존재하지 않습니다."),
    PARENT_NOT_FOUND("해당 부모 댓글이 조회되지 않습니다."),
    POST_NOT_FOUND("해당 게시물이 존재하지 않습니다."),
    COMMENT_NOT_FOUND("해당 댓글이 존재하지 않습니다."),
    CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다."),
    COMMENT_DELETE_FAIL("댓글 삭제를 실패했습니다."),
    STUDY_GROUP_FULL("스터디 그룹 인원이 다 찼습니다."),
    STUDY_GROUP_CANCEL_FAIL("스터디 그룹 삭제 실패"),
    POINT_NOT_ENOUGH("포인트가 부족합니다."),
    POINT_CHARGE_NOT_FOUND(" 충전 내역 조회 실패"),
    CHARGE_FAILED("충전 실패"),
    LEADER_ENROLLMENT_ERROR("스터디그룹 리더는 이미 참여 상태입니다."),
    DUPLICATE_ENROLLMENT_ERROR("해당 스터디 그룹에 이미 신청한 상태입니다."),
    ENROLLMENT_NOT_FOUND("해당 신청 정보가 없습니다."),
    RECRUITMENT_DATE_END("모집 기간이 아닙니다."),
    ENROLLMENT_CANCEL_FAIL("해당 스터디가 진행되면 신청 취소가 불가합니다."),
    STUDY_GROUP_UPDATE_FAIL("스터디그룹을 생성한 사람만 수정할 수 있습니다."),
    REFUND_FAIL("환불 실패");

    private final String description;
}