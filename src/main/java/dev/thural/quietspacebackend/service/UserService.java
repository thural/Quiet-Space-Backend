package dev.thural.quietspacebackend.service;

import dev.thural.quietspacebackend.model.UserDTO;
import dev.thural.quietspacebackend.request.LoginRequest;
import dev.thural.quietspacebackend.response.AuthResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Page<UserDTO> listUsers(String username, Integer pageNumber, Integer pageSize);

    AuthResponse addOne(UserDTO user);

    Optional<UserDTO> getById(UUID id);

    Optional<UserDTO> updateOne(UUID id, UserDTO user);

    Boolean deleteOne(UUID userId, String jwtToken);

    void patchOne(UUID id, UserDTO user, String jwtToken);

    Page<UserDTO> listUsersByQuery(String query, Integer pageNumber, Integer pageSize);

    AuthResponse getByLoginRequest(LoginRequest loginRequest);

    Optional<UserDTO> findUserByJwt(String jwt);
}
