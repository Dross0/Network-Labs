package message;

import chatnode.NetNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReplacementNodeShareMessage extends Message{
    private final NetNode replacementNode;

    public ReplacementNodeShareMessage(@NotNull NetNode senderNode,
                                       @NotNull NetNode receiverNode,
                                       @NotNull NetNode replacementNode) {
        super("", MessageType.REPLACEMENT_NODE_SHARE, senderNode, receiverNode);
        this.replacementNode = Objects.requireNonNull(receiverNode, "Replacement node cant be null");
    }

    public NetNode getReplacementNode() {
        return replacementNode;
    }
}
