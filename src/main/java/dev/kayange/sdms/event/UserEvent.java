package dev.kayange.sdms.event;

import dev.kayange.sdms.entity.UserEntity;
import dev.kayange.sdms.enumeration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @AllArgsConstructor
public class UserEvent {
    private UserEntity user;
    private EventType type;
    private Map<?, ?> data;
}
