package protocol;

import common.constants.MessageType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response extends SysMessage {
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data_) {
        return Response.builder()
                .code(1)
                .data(data_)
                .build();
    }

    public static Response fail(String msg) {
        return Response.builder()
                .code(0)
                .message(msg)
                .build();
    }

    @Override
    public int getMessageType() {
        return MessageType.RESPONSE;
    }
}
