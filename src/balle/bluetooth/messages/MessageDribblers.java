package balle.bluetooth.messages;

public class MessageDribblers extends AbstractSingleArgMessage {
	public static final int    OPCODE = 6;
    public static final String NAME   = "DRIBBLERS";
	
    public MessageDribblers(int onOff) throws InvalidArgumentException {
    	super(onOff);
        if ((onOff != 0) && (onOff != 1))
            throw new InvalidArgumentException("onOff should either be 0 or 1");
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
