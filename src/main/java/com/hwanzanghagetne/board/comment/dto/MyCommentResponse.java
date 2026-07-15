package com.hwanzanghagetne.board.comment.dto;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String content,
        Long postId,
        String postTitle,
        LocalDateTime createdAt
) {
}
