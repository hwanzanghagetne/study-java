package com.hwanzanghagetne.board.postlike;

import com.hwanzanghagetne.board.postlike.dto.PostLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PutMapping("/{postId}/likes")
    public ResponseEntity<Void> like(@PathVariable Long postId, Authentication authentication) {
        postLikeService.like(postId, authentication.getName());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlike(@PathVariable Long postId, Authentication authentication) {
        postLikeService.unlike(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{postId}/likes")
    public PostLikeResponse getLikeStatus(@PathVariable Long postId, Authentication authentication) {
        return postLikeService.getLikeStatus(postId, authentication.getName());
    }

}
