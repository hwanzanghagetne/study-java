package com.hwanzanghagetne.board.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.member")
    Page<Post> findAllWithMember(Pageable pageable);

    Page<Post> findByMemberLoginId(String loginId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.title LIKE %:keyword%")
    Page<Post> findByTitleContainingWithMember(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.content LIKE %:keyword%")
    Page<Post> findByContentContainingWithMember(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> findByTitleContainingOrContentContainingWithMember(@Param("keyword") String keyword, Pageable pageable);
}
