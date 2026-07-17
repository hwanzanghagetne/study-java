package com.hwanzanghagetne.board.postlike;

import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import com.hwanzanghagetne.board.post.Post;
import com.hwanzanghagetne.board.post.PostRepository;
import com.hwanzanghagetne.board.postlike.dto.PostLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void like(Long postId, String loginId) {


        if (postLikeRepository.existsByPostIdAndMemberLoginId(postId, loginId)) {
            return;
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void unlike(Long postId, String loginId) {
        postLikeRepository.findByPostIdAndMemberLoginId(postId, loginId)
                .ifPresent(postLikeRepository::delete);
    }


    @Transactional(readOnly = true)
    public PostLikeResponse getLikeStatus(Long postId, String loginId) {
        long likeCount = postLikeRepository.countByPostId(postId);
        boolean liked = postLikeRepository.existsByPostIdAndMemberLoginId(postId, loginId);
        return new PostLikeResponse(liked, likeCount);
    }
}
