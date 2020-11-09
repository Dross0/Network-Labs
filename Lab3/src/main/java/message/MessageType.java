package message;

public enum  MessageType {
    TEXT(true),
    ALIVE(false),
    CONNECT(true),
    CONFIRM(false),
    REPLACEMENT_NODE_SHARE(true);

    private final boolean needsConfirmation;

    MessageType(boolean needsConfirmation){
        this.needsConfirmation = needsConfirmation;
    }

    public boolean isNeedsConfirmation() {
        return needsConfirmation;
    }
}
