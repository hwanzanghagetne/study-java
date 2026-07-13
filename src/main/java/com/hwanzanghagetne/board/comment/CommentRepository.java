package com.hwanzanghagetne.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long id);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.post.id = :postId")
    List<Comment> findByPostIdWithMember(@Param("postId") Long postId);
}
