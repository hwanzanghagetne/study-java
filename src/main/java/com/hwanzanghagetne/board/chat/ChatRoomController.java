package com.hwanzanghagetne.board.chat;

import com.hwanzanghagetne.board.chat.dto.ChatMessageResponse;
import com.hwanzanghagetne.board.chat.dto.CreateDirectRoomRequest;
import com.hwanzanghagetne.board.chat.dto.CreateGroupRoomRequest;
import com.hwanzanghagetne.board.member.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/api/chat/rooms/direct")
    public ResponseEntity<Long> createOrGetDirectRoom(
            @AuthenticationPrincipal CustomUserDetails principal,
           @Valid @RequestBody CreateDirectRoomRequest request
    ) {
        Long roomId = chatRoomService.getOrCreateDirectRoom(principal.getId(), request.targetLoginId());
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessage(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<ChatMessageResponse> message = chatMessageService.getMessage(roomId, principal.getId());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/api/chat/rooms/group")
    public ResponseEntity<Long> createGroupRoom(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateGroupRoomRequest request
    ) {
        Long groupRoom = chatRoomService.createGroupRoom(principal.getId(), request.memberLoginIds());
        return ResponseEntity.ok(groupRoom);
    }
}
