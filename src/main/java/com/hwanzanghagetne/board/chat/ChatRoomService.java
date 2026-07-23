package com.hwanzanghagetne.board.chat;

import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final DirectRoomCreator directRoomCreator;


    public Long getOrCreateDirectRoom(Long myMemberId, String targetLoginId) {
        Member target = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Long member1Id = Math.min(myMemberId, target.getId());
        Long member2Id = Math.max(myMemberId, target.getId());

        return chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)
                .map(ChatRoom::getId)
                .orElseGet(() -> {
                    try {
                        return directRoomCreator.create(member1Id, member2Id);
                    } catch (DataIntegrityViolationException e) {
                        return chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)
                                .map(ChatRoom::getId)
                                .orElseThrow(() -> e);
                    }
                });
    }

    @Transactional
    public Long createGroupRoom(Long creatorId, List<String> memberLoginIds) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomType(RoomType.GROUP)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        Member creator = memberRepository.findById(creatorId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(savedRoom).member(creator).build());

        for (String loginId : memberLoginIds) {
            Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            if (member.getId().equals(creatorId)) {
                continue;
            }
            chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(savedRoom).member(member).build());
        }
        return savedRoom.getId();
    }

}
