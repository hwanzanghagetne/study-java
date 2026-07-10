package com.hwanzanghagetne.board.member.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String email
) {
}
