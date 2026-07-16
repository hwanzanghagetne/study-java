package com.hwanzanghagetne.board.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long id);

    void deleteByPostId(Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.post.id = :postId")
    List<Comment> findByPostIdWithMember(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.member.loginId = :loginId")
    Page<Comment> findByMemberLoginIdWithPost(@Param("loginId") String loginId, Pageable pageable);

    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);

    void deleteByParentId(Long parentId);
}
