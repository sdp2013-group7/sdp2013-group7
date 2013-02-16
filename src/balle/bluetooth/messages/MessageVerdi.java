package balle.bluetooth.messages;

public class MessageVerdi extends AbstractSingleArgMessage {
	public static final int    OPCODE = 7;
    public static final String NAME   = "PLAY VERDI";	
	
    public MessageVerdi() throws InvalidArgumentException {
    	super(0);
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
