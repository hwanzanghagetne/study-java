package com.hwanzanghagetne.board.post.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        int viewCount,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
