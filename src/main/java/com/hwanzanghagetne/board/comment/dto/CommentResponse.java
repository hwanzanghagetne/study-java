package com.hwanzanghagetne.board.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        String authorLoginId,
        String authorNickname,
        LocalDateTime createdAt,
        List<CommentResponse> replies

) {
}
