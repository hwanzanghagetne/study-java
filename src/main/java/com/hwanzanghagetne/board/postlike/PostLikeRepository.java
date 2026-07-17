package com.hwanzanghagetne.board.postlike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndMemberLoginId(Long postId, String loginId);

    Optional<PostLike> findByPostIdAndMemberLoginId(Long postId, String loginId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
