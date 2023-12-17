package protocol;

import java.io.Serializable;

/**
 * +----------------------------------------------------------------------------------------------------------------+
 * |                  magic number 4byte                    |                 serialization 4byte                   |
 * +----------------------------------------------------------------------------------------------------------------+
 * |                 message type 4byte                    |                data length 4byte                       |                                          |
 * +----------------------------------------------------------------------------------------------------------------+
 */
public abstract class SysMessage implements Serializable {
    public static final int MAGIC = 114514;
    public static final int HEADER_LENGTH = 16;

    public abstract int getMessageType();
}
