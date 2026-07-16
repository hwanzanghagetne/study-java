package com.hwanzanghagetne.board.comment;

import com.hwanzanghagetne.board.comment.dto.CommentResponse;
import com.hwanzanghagetne.board.comment.dto.MyCommentResponse;
import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.Member;
import com.hwanzanghagetne.board.member.MemberRepository;
import com.hwanzanghagetne.board.post.Post;
import com.hwanzanghagetne.board.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createComment(Long postId, String loginId, Long parentId,  String content) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

            if (parent.getParent() != null) {
                throw new BusinessException(ErrorCode.CANNOT_REPLY_TO_REPLY);
         }
        }
        Comment comment = Comment.builder()
                .content(content)
                .member(member)
                .post(post)
                .parent(parent)
                .build();
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
        }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdWithMember(postId);

        Map<Long, List<Comment>> repliesByParentId = comments.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return comments.stream()
                .filter(c -> c.getParent() == null)
                .map(comment -> toCommentResponse(comment, repliesByParentId.getOrDefault(comment.getId(), List.of())))
                .toList();
    }
        private CommentResponse toCommentResponse (Comment comment, List<Comment> replies) {
            List<CommentResponse> replyResponses = replies.stream()
                    .map(reply -> toCommentResponse(reply, List.of()))
                    .toList();

            return new CommentResponse(
                    comment.getId(),
                    comment.getContent(),
                    comment.getMember().getLoginId(),
                    resolveNickname(comment.getMember()),
                    comment.getCreatedAt(),
                    replyResponses
            );
    }

    @Transactional(readOnly = true)
    public Page<MyCommentResponse> getMyComments(String loginId, Pageable pageable) {
        Page<Comment> myComments = commentRepository.findByMemberLoginIdWithPost(loginId, pageable);
        return myComments.map(comment -> new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getPost().getTitle(),
                comment.getCreatedAt()
        ));
    }

    @Transactional
    public void deleteComment(Long commentId, String loginId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getMember().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.NOT_AUTHOR);
        }
        commentRepository.deleteByParentId(commentId);
        commentRepository.delete(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String loginId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getMember().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.NOT_AUTHOR);
        }
        comment.updateComment(content);
    }


    private String resolveNickname(Member member) {
        return member.isDeleted() ? "탈퇴한 회원" : member.getNickname();
    }

}
