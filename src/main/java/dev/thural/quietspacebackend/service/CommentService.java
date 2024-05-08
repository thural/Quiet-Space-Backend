package dev.thural.quietspacebackend.service;

import dev.thural.quietspacebackend.model.response.CommentLikeResponse;
import dev.thural.quietspacebackend.model.request.CommentRequest;
import dev.thural.quietspacebackend.model.response.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentService {
    Page<CommentResponse> getCommentsByPost(UUID postId, Integer pageNumber, Integer pageSize);

    void createComment(CommentRequest comment);

    Optional<CommentResponse> getCommentById(UUID id);

    void updateComment(UUID commentId, CommentRequest comment);
    
    void deleteComment(UUID id);

    void patchComment(UUID id, CommentRequest comment);

    void toggleCommentLike(UUID commentId);

    List<CommentLikeResponse> getLikesByCommentId(UUID commentId);

    List<CommentLikeResponse> getAllByUserId(UUID userId);
}
