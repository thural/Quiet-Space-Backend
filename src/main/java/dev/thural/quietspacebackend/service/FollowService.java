package dev.thural.quietspacebackend.service;

import dev.thural.quietspacebackend.model.FollowDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FollowService {

    Page<FollowDto> listFollowings(Integer pageNumber, Integer pageSize);

    Page<FollowDto> listFollowers(Integer pageNumber, Integer pageSize);

    void toggleFollow(UUID followedUserId);
}
