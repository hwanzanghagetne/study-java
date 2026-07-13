package com.hwanzanghagetne.board.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.member")
    Page<Post> findAllWithMember(Pageable pageable);

    Page<Post> findByMemberLoginId(String loginId, Pageable pageable);
}
