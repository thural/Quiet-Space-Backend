package dev.thural.quietspace.controller;

import dev.thural.quietspace.model.request.CommentRequest;
import dev.thural.quietspace.model.response.CommentResponse;
import dev.thural.quietspace.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    public static final String COMMENT_PATH = "/api/v1/comments";
    public static final String COMMENT_PATH_ID = "/{commentId}";

    private final CommentService commentService;


    @GetMapping("/post/{postId}")
    Page<CommentResponse> getCommentsByPostId(
            @PathVariable UUID postId,
            @RequestParam(name = "page-number", required = false) Integer pageNumber,
            @RequestParam(name = "page-size", required = false) Integer pageSize
    ) {
        return commentService.getCommentsByPostId(postId, pageNumber, pageSize);
    }

    @GetMapping("/user/{userId}")
    Page<CommentResponse> getCommentsByUserId(
            @PathVariable UUID userId,
            @RequestParam(name = "page-number", required = false) Integer pageNumber,
            @RequestParam(name = "page-size", required = false) Integer pageSize
    ) {
        return commentService.getCommentsByUserId(userId, pageNumber, pageSize);
    }

    @GetMapping(COMMENT_PATH_ID + "/replies")
    Page<CommentResponse> getCommentRepliesById(
            @PathVariable UUID commentId,
            @RequestParam(name = "page-number", required = false) Integer pageNumber,
            @RequestParam(name = "page-size", required = false) Integer pageSize
    ) {
        return commentService.getRepliesByParentId(commentId, pageNumber, pageSize);
    }

    @GetMapping(COMMENT_PATH_ID)
    ResponseEntity<CommentResponse> getCommentById(@PathVariable UUID commentId) {
        return commentService.getCommentById(commentId)
                .map(comment -> ResponseEntity.ok().body(comment))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<CommentResponse> createComment(@RequestBody @Validated CommentRequest comment) {
        return ResponseEntity.ok(commentService.createComment(comment));
    }

    @PutMapping(COMMENT_PATH_ID)
    ResponseEntity<CommentResponse> putComment(
            @PathVariable UUID commentId,
            @RequestBody @Validated CommentRequest comment
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, comment));
    }

    @DeleteMapping(COMMENT_PATH_ID)
    ResponseEntity<?> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(COMMENT_PATH_ID)
    ResponseEntity<CommentResponse> patchComment(@PathVariable UUID commentId, @RequestBody CommentRequest comment) {
        return ResponseEntity.ok(commentService.patchComment(commentId, comment));
    }

}
