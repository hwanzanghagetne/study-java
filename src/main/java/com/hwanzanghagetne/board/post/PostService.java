package com.hwanzanghagetne.board.post;

import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import com.hwanzanghagetne.board.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    public Long createPost(String loginId, String title, String content) {

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Transactional
    public PostResponse readPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getMember().getLoginId(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> readPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllWithMember(pageable);
        return posts.map(post -> new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getMember().getLoginId(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        ));
    }

    @Transactional
    public void updatePost(Long postId, String loginId, String title, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.NOT_AUTHOR);
        }
        post.updateContent(title,content);
    }

    @Transactional
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.NOT_AUTHOR);
        }

        postRepository.delete(post);
    }
}
