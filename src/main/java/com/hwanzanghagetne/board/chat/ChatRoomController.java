package com.hwanzanghagetne.board.chat;

import com.hwanzanghagetne.board.chat.dto.CreateDirectRoomRequest;
import com.hwanzanghagetne.board.member.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/api/chat/rooms/direct")
    public ResponseEntity<Long> createOrGetDirectRoom(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody CreateDirectRoomRequest request
    ) {
        Long roomId = chatRoomService.getOrCreateDirectRoom(principal.getId(), request.targetLoginId());
        return ResponseEntity.ok(roomId);
    }
}
