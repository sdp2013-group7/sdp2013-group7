package balle.bluetooth.messages;

public class MessageForward extends AbstractSingleArgMessage {
    public static final int    OPCODE = 4;
    public static final String NAME   = "FORWARD";

    public MessageForward(int speed) throws InvalidArgumentException {
        super(speed);
    }

	@Override
	public int getOpcode() {
		return OPCODE;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
