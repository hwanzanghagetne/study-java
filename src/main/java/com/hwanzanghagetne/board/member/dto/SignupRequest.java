package com.hwanzanghagetne.board.member.dto;

public record SignupRequest(
        String loginId,
        String password,
        String name,
        String nickname,
        String email
) {
}
