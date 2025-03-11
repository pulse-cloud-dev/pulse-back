package pulse.back.domain.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pulse.back.common.enums.ResultCodes;
import pulse.back.common.response.ResultData;
import pulse.back.domain.chat.dto.response.RoomResponse;
import pulse.back.domain.chat.service.ChatService;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    /**
     * 결제가 완료되면 멘티를 채팅방에 추가한다
     */
    @GetMapping("/join/{roomId}")
    public Mono<ResultData<ResultCodes>> addMemberToRoom(@PathVariable("roomId") String roomId) {
        return chatService.addMemberToRoom(roomId)
                .thenReturn(new ResultData<>(ResultCodes.SUCCESS, "채팅방에 참가되었습니다."));
    }

    /**
     * 참여중인 채팅방 목록을 불러온다
     */
    @GetMapping("/list")
    public Mono<ResultData<List<RoomResponse>>> getRoomList() {
        return chatService.getRoomList()
                .map(RoomResponse::from)
                .collectList()
                .map(roomResponseList -> new ResultData<>(roomResponseList, "입장한 모든 채팅방을 제공합니다."));
    }
}
