package com.hwanzanghagetne.board.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long chatRoomId,
        String senderLoginId,
        String senderNickname,
        String content,
        LocalDateTime createdAt
) {
}
