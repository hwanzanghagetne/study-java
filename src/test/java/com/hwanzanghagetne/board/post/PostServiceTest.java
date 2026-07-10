package com.hwanzanghagetne.board.post;

import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    @DisplayName("게시글 작성에 성공하면 저장된 게시글의 id를 반환한다")
    @Test
    void createPost_성공(){
        // given
        Member member = Member.builder()
                .loginId("test1234")
                .password("encoded")
                .name("홍길동")
                .nickname("길동이")
                .email("test@test.com")
                .build();

        when(memberRepository.findByLoginId("test1234"))
                .thenReturn(Optional.of(member));

        Post savedPost = Post.builder()
                .member(member)
                .title("제목")
                .content("내용")
                .build();
        when(postRepository.save(any(Post.class)))
                .thenReturn(savedPost);
        // when
        Long resultId = postService.createPost("test1234", "제목", "내용");

        // then
        assertThat(resultId).isEqualTo(savedPost.getId());
    }

    @DisplayName("회원 못 찾으면 예외 터진다.")
    @Test
    void createPost_실패() {
        // given
        when(memberRepository.findByLoginId("test1234"))
                .thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> postService.createPost("test1234", "제목", "내용"))
                .isInstanceOf(BusinessException.class);
    }
}
