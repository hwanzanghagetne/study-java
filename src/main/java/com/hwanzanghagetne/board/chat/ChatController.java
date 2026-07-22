package com.hwanzanghagetne.board.chat;

import com.hwanzanghagetne.board.chat.dto.ChatMessageResponse;
import com.hwanzanghagetne.board.chat.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String echo(String message) {
        return message;
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request, Principal principal) {

        ChatMessageResponse response = chatMessageService.saveMessage(roomId, principal.getName(), request.content());
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, response);
    }
}
