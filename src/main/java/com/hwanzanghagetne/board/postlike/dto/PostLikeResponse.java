package com.hwanzanghagetne.board.postlike.dto;

public record PostLikeResponse(
        boolean liked,
        long likeCount
) {
}
