package dev.thural.quietspacebackend.service;

import dev.thural.quietspacebackend.model.PostDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    List<PostDTO> getAll();

    PostDTO addOne(PostDTO post);

    Optional<PostDTO> getById(UUID id);

    void updateOne(UUID id, PostDTO post);

    void deleteOne(UUID id);

    void patchOne(UUID id, PostDTO post);
}
