package dev.thural.quietspacebackend.controller;

import dev.thural.quietspacebackend.model.UserDto;
import dev.thural.quietspacebackend.model.request.LoginRequest;
import dev.thural.quietspacebackend.model.response.AuthResponse;
import dev.thural.quietspacebackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    ResponseEntity<?> signupUser(@Validated @RequestBody UserDto user) {

        AuthResponse authResponse = authService.register(user);
        String userId = authResponse.getUserId();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UserController.USER_PATH + "/" + userId);
        return new ResponseEntity<>(authResponse, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        AuthResponse authResponse = authService.login(loginRequest);
        String userId = authResponse.getUserId();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UserController.USER_PATH + "/" + userId);
        return new ResponseEntity<>(authResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader) {

        authService.logout(authHeader);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
