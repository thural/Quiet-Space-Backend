package dev.thural.quietspacebackend.service.impls;

import dev.thural.quietspacebackend.controller.NotFoundException;
import dev.thural.quietspacebackend.utils.JwtProvider;
import dev.thural.quietspacebackend.entity.UserEntity;
import dev.thural.quietspacebackend.mapper.UserMapper;
import dev.thural.quietspacebackend.model.UserDTO;
import dev.thural.quietspacebackend.repository.UserRepository;
import dev.thural.quietspacebackend.request.LoginRequest;
import dev.thural.quietspacebackend.response.AuthResponse;
import dev.thural.quietspacebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

import static dev.thural.quietspacebackend.utils.CustomPageProvider.buildCustomPageRequest;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public Page<UserDTO> listUsers(String username, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = buildCustomPageRequest(pageNumber, pageSize);

        Page<UserEntity> userPage;

        if (StringUtils.hasText(username)) {
            userPage = userRepository.findAllByUsernameIsLikeIgnoreCase("%" + username + "%", pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        return userPage.map(userMapper::userEntityToDto);
    }

    @Override
    public Page<UserDTO> listUsersByQuery(String query, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildCustomPageRequest(pageNumber, pageSize);

        Page<UserEntity> userPage;

        if (StringUtils.hasText(query)) {
            userPage = userRepository.findAllByQuery(query, pageRequest);
        } else {
            userPage = userRepository.findAll(pageRequest);
        }

        return userPage.map(userMapper::userEntityToDto);
    }

    @Override
    public AuthResponse addOne(UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity userEntity = userMapper.userDtoToEntity(user);
        UserDTO savedUser = userMapper.userEntityToDto(userRepository.save(userEntity));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        String token = JwtProvider.generatedToken(authentication);
        return new AuthResponse(token, "register success", savedUser.getId().toString());
    }

    @Override
    public AuthResponse getByLoginRequest(LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String token = JwtProvider.generatedToken(authentication);

        Optional<UserEntity> optionalUser = userRepository.findUserEntityByEmail(loginRequest.getEmail());
        String userId = optionalUser.isPresent() ? optionalUser.get().getId().toString() : "null";
        return new AuthResponse(token, "login success", userId);
    }

    @Override
    public Optional<UserDTO> findUserByJwt(String jwt) {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        UserEntity userEntity = userRepository.findUserEntityByEmail(email).orElseThrow(NotFoundException::new);
        return Optional.of(userMapper.userEntityToDto(userEntity));
    }

    Authentication authenticate(String email, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails == null)
            throw new BadCredentialsException("invalid username");

        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("invalid password");

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }

    @Override
    public Optional<UserDTO> getById(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(NotFoundException::new);
        UserDTO userDTO = userMapper.userEntityToDto(userEntity);
        return Optional.of(userDTO);
    }

    @Override
    public Optional<UserDTO> updateOne(UUID id, UserDTO user) {
        UserEntity foundUser = userRepository.findById(id).orElseThrow(NotFoundException::new);

        UserDTO userDTO = userMapper.userEntityToDto(foundUser);
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());

        UserEntity updatedUser = userRepository.save(userMapper.userDtoToEntity(userDTO));

        return Optional.of(userMapper.userEntityToDto(updatedUser));
    }

    @Override
    public Boolean deleteOne(UUID userId, String jwtToken) {

        String loggedUserEmail = JwtProvider.getEmailFromJwtToken(jwtToken);
        UserEntity loggedUser = userRepository.findUserEntityByEmail(loggedUserEmail)
                .orElseThrow(NotFoundException::new);

        if (loggedUser != null) {
            userRepository.deleteById(userId);
            return true;
        }

        return false;
    }

    @Override
    public void patchOne(UUID id, UserDTO user, String jwtToken) {
        String loggedUserEmail = JwtProvider.getEmailFromJwtToken(jwtToken);
        UserEntity loggedUserEntity = userRepository.findUserEntityByEmail(loggedUserEmail)
                .orElseThrow(NotFoundException::new);

        UserDTO loggedUserDTO = userMapper.userEntityToDto(loggedUserEntity);
        if (StringUtils.hasText(user.getUsername()))
            loggedUserDTO.setUsername(user.getUsername());
        if (StringUtils.hasText(user.getPassword()))
            loggedUserDTO.setPassword(user.getPassword());
        userRepository.save(userMapper.userDtoToEntity(loggedUserDTO));
    }

}