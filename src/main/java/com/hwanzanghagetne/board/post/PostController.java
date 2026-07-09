package com.hwanzanghagetne.board.post;

import com.hwanzanghagetne.board.post.dto.CreatePostRequest;
import com.hwanzanghagetne.board.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody CreatePostRequest request, Authentication authentication) {

        Long postId = postService.createPost(authentication.getName(), request.title(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.readPost(id));
    }
}
