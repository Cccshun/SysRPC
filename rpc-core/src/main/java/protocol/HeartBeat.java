package protocol;

import common.constants.MessageType;

public class HeartBeat extends SysMessage{
    @Override
    public int getMessageType() {
        return MessageType.HeartBeat;
    }
}
