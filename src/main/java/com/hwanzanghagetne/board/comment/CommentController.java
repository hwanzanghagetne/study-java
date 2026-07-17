package com.hwanzanghagetne.board.comment;

import com.hwanzanghagetne.board.comment.dto.CommentResponse;
import com.hwanzanghagetne.board.comment.dto.CreateCommentRequest;
import com.hwanzanghagetne.board.comment.dto.MyCommentResponse;
import com.hwanzanghagetne.board.comment.dto.UpdateCommentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Long> createComment(@PathVariable Long postId, @RequestBody @Valid CreateCommentRequest request, Authentication authentication) {
        Long commentId = commentService.createComment(postId, authentication.getName(), request.parentId(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }


    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        commentService.deleteComment(commentId, authentication.getName(),isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> updateContent(@PathVariable Long commentId, Authentication authentication, @RequestBody @Valid UpdateCommentRequest request) {
        commentService.updateComment(commentId, authentication.getName(), request.content());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/members/me/comments")
    public Page<MyCommentResponse> getMyComments(Authentication authentication, Pageable pageable) {
        return commentService.getMyComments(authentication.getName(), pageable);
    }
}
