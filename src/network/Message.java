package network;

import java.io.Serializable;

/**
 * Represents a message sent between client and server
 **/
public record Message(String message, MESSAGE_TYPE messageType) implements Serializable {
    public enum MESSAGE_TYPE {
        NO_ACTION,
        ACTION_REQUESTED,
        GAME_OVER,
    }

    public boolean actionExpected() {
        return messageType == MESSAGE_TYPE.ACTION_REQUESTED;
    }

    public boolean gameOver() {
        return messageType == MESSAGE_TYPE.GAME_OVER;
    }
}
