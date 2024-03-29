package dev.thural.quietspacebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.thural.quietspacebackend.entity.CommentEntity;
import dev.thural.quietspacebackend.entity.PostLikeEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private UUID id;

    String username;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    private String text;

    UUID userId;

    private List<PostLikeEntity> likes;

    @JsonIgnore
    private List<CommentEntity> comments;

    private OffsetDateTime createDate;

    private OffsetDateTime updateDate;

}