package com.hwanzanghagetne.board.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {

@DisplayName("조회수가 1 증가한다.")
@Test
void increaseViewCount() {
    // given
    Post post = Post.builder()
            .title("제목")
            .content("내용")
            .build();
    // when
    post.increaseViewCount();
    // then
    assertThat(post.getViewCount()).isEqualTo(1);
}
}
