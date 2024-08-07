package dev.thural.quietspace.mapper;

import dev.thural.quietspace.entity.User;
import dev.thural.quietspace.model.request.UserRegisterRequest;
import dev.thural.quietspace.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    UserResponse userEntityToResponse(User user);

    UserRegisterRequest userEntityToRequest(User user);

}
