package com.hwanzanghagetne.board.chat;

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
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public Long getOrCreateDirectRoom(Long myMemberId, String targetLoginId) {
        Member target = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Long member1Id = Math.min(myMemberId, target.getId());
        Long member2Id = Math.max(myMemberId, target.getId());

        return chatRoomRepository.findByMember1IdAndMember2Id(member1Id, member2Id)
                .map(ChatRoom::getId)
                .orElseGet(() -> createDirectRoom(member1Id, member2Id));
    }

    private Long createDirectRoom(Long member1Id, Long member2Id) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomType(RoomType.DIRECT)
                .member1Id(member1Id)
                .member2Id(member2Id)
                .build();
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        Member member1 = memberRepository.findById(member1Id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        Member member2 = memberRepository.findById(member2Id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(savedRoom).member(member1).build());
        chatRoomMemberRepository.save(ChatRoomMember.builder().chatRoom(savedRoom).member(member2).build());

        return savedRoom.getId();
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
