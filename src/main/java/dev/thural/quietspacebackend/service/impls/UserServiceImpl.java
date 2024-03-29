package dev.thural.quietspacebackend.service.impls;

import dev.thural.quietspacebackend.entity.TokenEntity;
import dev.thural.quietspacebackend.exception.UserNotFoundException;
import dev.thural.quietspacebackend.model.UserDto;
import dev.thural.quietspacebackend.repository.TokenRepository;
import dev.thural.quietspacebackend.utils.PagingProvider;
import dev.thural.quietspacebackend.entity.UserEntity;
import dev.thural.quietspacebackend.mapper.UserMapper;
import dev.thural.quietspacebackend.repository.UserRepository;
import dev.thural.quietspacebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public Page<UserDto> listUsers(String username, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PagingProvider.buildCustomPageRequest(pageNumber, pageSize);
        Page<UserEntity> userPage;

        if (StringUtils.hasText(username)) {
            userPage = userRepository.findAllByUsernameIsLikeIgnoreCase("%" + username + "%", pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        return userPage.map(userMapper::userEntityToDto);
    }

    @Override
    public Page<UserDto> listUsersByQuery(String query, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PagingProvider.buildCustomPageRequest(pageNumber, pageSize);
        Page<UserEntity> userPage;

        if (StringUtils.hasText(query)) {
            userPage = userRepository.findAllByQuery(query, pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        return userPage.map(userMapper::userEntityToDto);
    }

    @Override
    public Optional<UserDto> findLoggedUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedUser = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        return Optional.of(userMapper.userEntityToDto(loggedUser));
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        UserDto userDTO = userMapper.userEntityToDto(userEntity);
        return Optional.of(userDTO);
    }

    @Override
    public Boolean deleteUser(UUID userId, String authHeader) {

        boolean isDeleted = false;
        String token = authHeader.substring(7);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedUser = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        if (loggedUser.getRole().equals("admin")){
            userRepository.deleteById(userId);
            isDeleted = true;
        } else if (loggedUser.getId().equals(userId)) {
            userRepository.deleteById(userId);
            isDeleted =  true;
        }

        if (isDeleted) tokenRepository.save(TokenEntity.builder()
                .jwtToken(token)
                .email(loggedUser.getEmail())
                .build()
        );

        return isDeleted;
    }

    @Override
    public void patchUser(UserDto userDTO) {

        UserEntity requestedUser = userRepository.findUserEntityByEmail(userDTO.getEmail())
                .orElseThrow(UserNotFoundException::new);

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ADMIN");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedUser = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        if (!isAdmin && !requestedUser.getEmail().equals(loggedUser.getEmail()))
            throw new AccessDeniedException("logged user has no access to requested resource");

        if (StringUtils.hasText(userDTO.getUsername()))
            loggedUser.setUsername(userDTO.getUsername());

        if (StringUtils.hasText(userDTO.getEmail()))
            loggedUser.setEmail(userDTO.getEmail());

        if (StringUtils.hasText(userDTO.getPassword()))
            loggedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(loggedUser);
    }

}