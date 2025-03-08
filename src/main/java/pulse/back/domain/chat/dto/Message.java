package pulse.back.domain.chat.dto;

public interface Message {
    MessageType messageType();
    Object payload();
}
