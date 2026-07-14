package com.hwanzanghagetne.board.member.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String nickname,
        @NotBlank String email
) {
}
