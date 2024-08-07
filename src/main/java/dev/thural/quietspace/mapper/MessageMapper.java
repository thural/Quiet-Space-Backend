package dev.thural.quietspace.mapper;

import dev.thural.quietspace.entity.Message;
import dev.thural.quietspace.model.request.MessageRequest;
import dev.thural.quietspace.model.response.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    Message toEntity(MessageRequest messageRequest);

    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "senderId", source = "sender.id")
    MessageResponse toResponse(Message messageEntity);

}