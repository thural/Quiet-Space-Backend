package dev.thural.quietspacebackend.service.impls;

import dev.thural.quietspacebackend.entity.Token;
import dev.thural.quietspacebackend.entity.User;
import dev.thural.quietspacebackend.mapper.UserMapper;
import dev.thural.quietspacebackend.model.request.UserRequest;
import dev.thural.quietspacebackend.model.request.LoginRequest;
import dev.thural.quietspacebackend.model.response.AuthResponse;
import dev.thural.quietspacebackend.repository.TokenRepository;
import dev.thural.quietspacebackend.repository.UserRepository;
import dev.thural.quietspacebackend.service.AuthService;
import dev.thural.quietspacebackend.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    @Override
    public AuthResponse register(UserRequest user) {
        String userPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(userMapper.userRequestToEntity(user));

        Authentication authentication = generateAuthentication(user.getEmail(), userPassword);

        String token = JwtProvider.generateToken(authentication);
        String userId = savedUser.getId().toString();
        return new AuthResponse(UUID.randomUUID(), token, userId, "register success");
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String userEmail = loginRequest.getEmail();
        String userPassword = loginRequest.getPassword();
        Authentication authentication = generateAuthentication(userEmail, userPassword);
        String token;

        if(tokenRepository.existsByEmail(userEmail)){
            token = tokenRepository.getByEmail(userEmail).getJwtToken();
            tokenRepository.deleteByEmail(userEmail);
        } else {
            token = JwtProvider.generateToken(authentication);
        }

        Optional<User> optionalUser = userRepository.findUserEntityByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new AuthenticationCredentialsNotFoundException("login failed"));
        return new AuthResponse(UUID.randomUUID(), token, user.getId().toString(), "login success");
    }

    @Override
    public void logout(String authHeader) {
        String currentUserName = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            addToBlacklist(authHeader, currentUserName);
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public Authentication generateAuthentication(String email, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (userDetails == null)
            throw new BadCredentialsException("invalid username");

        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("invalid password");

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    @Override
    public void addToBlacklist(String authHeader, String email) {
        String token = authHeader.substring(7);
        boolean isBlacklisted = tokenRepository.existsByJwtToken(token);
        if (!isBlacklisted) tokenRepository.save(Token.builder()
                .jwtToken(token)
                .email(email)
                .build()
        );
        SecurityContextHolder.clearContext();
    }

    @Override
    public boolean isBlacklisted(String authHeader) {
        String token = authHeader.substring(7);
        return tokenRepository.existsByJwtToken(token);
    }

}