package protocol;

import common.constants.MessageType;
import lombok.*;
import protocol.SysMessage;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request extends SysMessage {
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;

    @Override
    public int getMessageType() {
        return MessageType.REQUEST;
    }

}
