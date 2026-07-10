package com.hwanzanghagetne.board.post;

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
                .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));

        Post post = Post.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public PostResponse readPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> readPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        ));
    }
}
