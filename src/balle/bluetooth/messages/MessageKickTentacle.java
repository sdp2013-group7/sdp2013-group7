package balle.bluetooth.messages;

public class MessageKickTentacle extends AbstractSingleArgMessage {
	public static final int    OPCODE = 5;
    public static final String NAME   = "KICK TENT";
    
    public MessageKickTentacle(int leftRightBoth) throws InvalidArgumentException {
    	super(leftRightBoth);
    	if ((leftRightBoth != 0) && (leftRightBoth != 1) && (leftRightBoth != 2))
            throw new InvalidArgumentException("leftRightBoth should either be 0, 1 or 2");
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
