package com.project.bookstudy.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("이메일이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("유효성 검사 에러", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRATION_TOKEN("만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INCORRECT_APPROACH("올바르지 않은 접근입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("DB에 존재하지 않는 Refresh 토큰입니다.", HttpStatus.NOT_FOUND),
    AUTHORIZATION_NOT_FOUND("권한이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    LOGIN_REQUIRED("로그인이 필요합니다", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED("로그인에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    SUBJECT_ALREADY_EXISTS("동일한 제목이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    STUDY_GROUP_NOT_FOUND("해당 스터디 그룹이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PARENT_NOT_FOUND("해당 부모 댓글이 조회되지 않습니다.", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND("해당 게시물이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("해당 댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    COMMENT_DELETE_FAIL("댓글 삭제를 실패했습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_ALREADY_DELETED("이미 삭제된 댓글입니다.", HttpStatus.BAD_REQUEST),
    STUDY_GROUP_FULL("스터디 그룹 인원이 다 찼습니다.", HttpStatus.BAD_REQUEST),
    STUDY_GROUP_CANCEL_FAIL("스터디 그룹 삭제 실패", HttpStatus.BAD_REQUEST),
    POINT_NOT_ENOUGH("포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    POINT_CHARGE_NOT_FOUND(" 충전 내역 조회 실패", HttpStatus.NOT_FOUND),
    CHARGE_FAILED("충전 실패", HttpStatus.BAD_REQUEST),
    LEADER_ENROLLMENT_ERROR("스터디그룹 리더는 이미 참여 상태입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ENROLLMENT_ERROR("해당 스터디 그룹에 이미 신청한 상태입니다.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND("해당 신청 정보가 없습니다.", HttpStatus.NOT_FOUND),
    RECRUITMENT_DATE_END("모집 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
    ENROLLMENT_CANCEL_FAIL("해당 스터디가 진행되면 신청 취소 실패했습니다.", HttpStatus.UNAUTHORIZED),
    STUDY_GROUP_UPDATE_FAIL("스터디그룹을 생성한 사람만 수정할 수 있습니다.", HttpStatus.UNAUTHORIZED),
    POST_UPDATE_FAIL("게시글을 생성한 사람만 수정할 수 있습니다.", HttpStatus.UNAUTHORIZED),
    POST_DELETE_FAIL("게시글을 생성한 사람만 삭제할 수 있습니다.", HttpStatus.UNAUTHORIZED),
    REFUND_FAIL("환불 실패", HttpStatus.UNAUTHORIZED),

    S3_SERVICE_ERROR("S3 서비스 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_CLIENT_ERROR("S3 클라이언트 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_IMAGE_FILE_FAILED("이미지를 삭제하는데 실패했습니다.",HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_IMAGE_FILE_FAILED("이미지를 업로드하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String description;
    private final HttpStatus httpStatus;
}