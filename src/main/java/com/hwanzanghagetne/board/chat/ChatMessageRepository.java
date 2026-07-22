package com.hwanzanghagetne.board.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.member WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdAt ASC")
    List<ChatMessage> findByChatRoomIdWithMember(@Param("chatRoomId") Long chatRoomId);

}
