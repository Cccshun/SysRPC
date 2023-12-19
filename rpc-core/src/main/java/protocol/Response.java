package protocol;

import common.constants.MessageType;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response extends SysMessage {
    private String requestId;
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data, String requestId) {
        return Response.builder()
                .requestId(requestId)
                .code(1)
                .data(data)
                .build();
    }

    public static Response fail(String msg, String requestId) {
        return Response.builder()
                .requestId(requestId)
                .code(1)
                .message(msg)
                .build();
    }

    @Override
    public int getMessageType() {
        return MessageType.RESPONSE;
    }
}
