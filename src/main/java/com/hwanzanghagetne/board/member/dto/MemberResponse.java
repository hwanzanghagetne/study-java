package com.hwanzanghagetne.board.member.dto;

public record MemberResponse(
        String loginId,
        String name,
        String nickname,
        String email
) {
}
