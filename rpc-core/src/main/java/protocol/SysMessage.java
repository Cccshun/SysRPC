package protocol;

import java.io.Serializable;

/**
 +---------------------------+---------------------------+-----------------------------+-------------------------+
 |    Magic Number 4 bytes   |    Package Type 4 bytes   |   Serializer Type 4 bytes   |   Data Length 4 bytes   |
 +---------------------------+---------------------------+-----------------------------+-------------------------+
 |                                                 Data Bytes                                                    |
 +---------------------------------------------------------------------------------------------------------------+

 */
public abstract class SysMessage implements Serializable {
    public static final int MAGIC = 114514;
    public static final int HEADER_LENGTH = 16;

    public abstract int getMessageType();
}
