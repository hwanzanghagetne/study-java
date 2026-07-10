package com.hwanzanghagetne.board.comment;

import com.hwanzanghagetne.board.comment.dto.CommentResponse;
import com.hwanzanghagetne.board.comment.dto.CreateCommentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        Long commentId = commentService.createComment(postId, authentication.getName(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }


    @GetMapping("/api/posts/{postId}/comments")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
