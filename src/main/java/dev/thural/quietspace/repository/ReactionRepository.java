package dev.thural.quietspace.repository;

import dev.thural.quietspace.entity.Reaction;
import dev.thural.quietspace.model.response.ReactionResponse;
import dev.thural.quietspace.utils.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    List<Reaction> findAllByContentId(UUID contentId);

    List<Reaction> findAllByUserId(UUID userId);

    List<Reaction> findAllByContentIdAndUserId(UUID contentId, UUID userId);

    boolean existsByContentIdAndUserId(UUID contentId, UUID userId);

    Optional<Reaction> findByContentIdAndUserId(UUID commentId, UUID id);

    List<Reaction> findAllByContentTypeAndUserId(ContentType contentType, UUID userId);

    List<Reaction> findAllByContentIdAndContentType(UUID contentId, ContentType contentType);

    List<Reaction> findAllByUserIdAndContentType(UUID userId, ContentType contentType);
}