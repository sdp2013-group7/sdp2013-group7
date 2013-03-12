package balle.bluetooth.messages;

public class MessageMoveTentacle extends AbstractTwoArgMessage {
	public static final int    OPCODE = 8;
    public static final String NAME   = "MOVE TENT";
    
    public MessageMoveTentacle(int leftRightBoth, int extendRetract) throws InvalidArgumentException {
    	super(leftRightBoth, extendRetract);
    	if ((leftRightBoth != 0) && (leftRightBoth != 1) && (leftRightBoth != 2))
            throw new InvalidArgumentException("leftRightBoth should either be 0, 1 or 2");
    	if ((extendRetract != 0) && (extendRetract != 1))
    		throw new InvalidArgumentException("extendRetract should either be 0 or 1");
    }
    
    public int getTentacle() {
    	try {
    		return getArgument(0);
    	} catch (InvalidArgumentException e) {
    		return 2; // Shouldn't happen
    		// Will return both
    	}
    }
    
    public int getAction() {
    	try {
    		return getArgument(1);
    	} catch (InvalidArgumentException e) {
    		return 1; // Shouldn't happen
    		// Will retract
    	}
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
