package dev.thural.quietspace.websocket.service.impl;

import dev.thural.quietspace.mapper.UserMapper;
import dev.thural.quietspace.model.response.UserResponse;
import dev.thural.quietspace.repository.UserRepository;
import dev.thural.quietspace.utils.enums.StatusType;
import dev.thural.quietspace.websocket.model.UserRepresentation;
import dev.thural.quietspace.websocket.service.UserServiceWs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceWsImpl implements UserServiceWs {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void setOnlineStatus(String userEmail, StatusType type) {
        userRepository.findUserEntityByEmail(userEmail)
                .ifPresent((storedUser) -> {
                    storedUser.setStatusType(StatusType.OFFLINE);
                    userRepository.save(storedUser);
                });
    }

    @Override
    public List<UserResponse> findConnectedFollowings(UserRepresentation user) {
        return userRepository.findUserEntityByEmail(user.getEmail()).map(
                foundUser -> foundUser.getFollowings()
                        .stream()
                        .filter(following -> following.getStatusType().equals(StatusType.ONLINE))
                        .map(userMapper::userEntityToResponse)
                        .toList()
        ).orElse(List.of());
    }

}
