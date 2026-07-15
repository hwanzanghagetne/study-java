package com.hwanzanghagetne.board.post;

import com.hwanzanghagetne.board.comment.CommentService;
import com.hwanzanghagetne.board.comment.dto.CommentResponse;
import com.hwanzanghagetne.board.comment.dto.CreateCommentRequest;
import com.hwanzanghagetne.board.post.dto.CreatePostRequest;
import com.hwanzanghagetne.board.post.dto.PostResponse;
import com.hwanzanghagetne.board.post.dto.UpdatePostRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Long> createPost(@Valid @RequestBody CreatePostRequest request, Authentication authentication) {

        Long postId = postService.createPost(authentication.getName(), request.title(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readPost(@PathVariable Long id, HttpSession session) {
        Set<Long> viewedPosts = (Set<Long>) session.getAttribute("viewedPosts");
        if (viewedPosts == null) {
            viewedPosts = new HashSet<>();
            session.setAttribute("viewedPosts", viewedPosts);
        }

        if (!viewedPosts.contains(id)) {
            postService.increaseViewCount(id);
            viewedPosts.add(id);
        }
        return ResponseEntity.ok(postService.readPost(id));
    }

    @GetMapping
    public Page<PostResponse> getPosts(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false, defaultValue = "titleContent") String searchType,
                                       Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return postService.readPosts(pageable);
        }
        return postService.searchPosts(keyword, searchType, pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @Valid @RequestBody UpdatePostRequest request, Authentication authentication) {
        postService.updatePost(id, authentication.getName(), request.title(), request.content());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }


}
