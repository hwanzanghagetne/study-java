package com.hwanzanghagetne.board.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String authorLoginId,
        String authorNickname,
        LocalDateTime createdAt

) {
}
