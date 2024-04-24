package dev.thural.quietspacebackend.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    @NotNull
    private List<UUID> userIds;
    @NotNull
    private MessageRequest message;

}
