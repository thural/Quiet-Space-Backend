package dev.thural.quietspacebackend.controller;

import dev.thural.quietspacebackend.model.MessageDTO;
import dev.thural.quietspacebackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    public static final String MESSAGE_PATH = "/api/v1/messages";
    public static final String MESSAGE_PATH_ID = MESSAGE_PATH + "/{messageId}";

    private final MessageService messageService;


    @RequestMapping(value = MESSAGE_PATH, method = RequestMethod.POST)
    ResponseEntity createMessage(@RequestHeader("Authorization") String jwtToken,
                                 @RequestBody @Validated MessageDTO messageDTO) {

        MessageDTO savedMessage = messageService.addOne(messageDTO, jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", MESSAGE_PATH + "/" + savedMessage.getId());
        return new ResponseEntity(savedMessage, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = MESSAGE_PATH_ID, method = RequestMethod.DELETE)
    ResponseEntity deleteMessage(@RequestHeader("Authorization") String jwtToken,
                                 @PathVariable("messageId") UUID messageId) {
        messageService.deleteOne(messageId, jwtToken);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
