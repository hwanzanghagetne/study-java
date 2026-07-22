package com.hwanzanghagetne.board.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMember1IdAndMember2Id(Long member1Id, Long member2Id);
}
