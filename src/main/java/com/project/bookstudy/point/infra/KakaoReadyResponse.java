package com.project.bookstudy.point.infra;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class KakaoReadyResponse {
    private String tid;
    private String next_redirect_pc_url;
    private String created_at;
    private String memberId;

    @Builder
    private KakaoReadyResponse(String tid, String next_redirect_pc_url, String created_at, String memberId) {
        this.tid = tid;
        this.next_redirect_pc_url = next_redirect_pc_url;
        this.created_at = created_at;
        this.memberId = memberId;
    }
}
