package com.hwanzanghagetne.board.chat;

import com.hwanzanghagetne.board.chat.dto.ChatMessageResponse;
import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, String senderLoginId, String content) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        Member sender = memberRepository.findByLoginId(senderLoginId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .content(content)
                .chatRoom(chatRoom)
                .member(sender)
                .build();

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return new ChatMessageResponse(
                saved.getId(),
                chatRoom.getId(),
                sender.getLoginId(),
                sender.getNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessage(Long roomId, Long requestId) {
        boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndMemberId(roomId, requestId);
        if (!isMember) {
            throw new BusinessException(ErrorCode.NOT_CHAT_ROOM_MEMBER);
        }

        List<ChatMessage> message = chatMessageRepository.findByChatRoomIdWithMember(roomId);

        return message.stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(),
                        roomId,
                        m.getMember().getLoginId(),
                        m.getMember().getNickname(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }
}
