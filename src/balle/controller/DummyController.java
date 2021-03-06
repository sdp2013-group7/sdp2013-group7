/**
 * 
 */
package balle.controller;

/**
 * @author s0909773
 * 
 */
public class DummyController implements Controller {

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#backward(int)
     */
    @Override
    public void backward(int speed) {
        System.out.println("Backward " + speed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#forward(int)
     */
    @Override
    public void forward(int speed) {
        System.out.println("Forward " + speed);

    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#floatWheels()
     */
    @Override
    public void floatWheels() {
        System.out.println("FloatWheels");
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#stop()
     */
    @Override
    public void stop() {
        System.out.println("Stop");

    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#rotate(int, int)
     */
    @Override
    public void rotate(int deg, int speed) {
        System.out.println("Rotate (deg: " + deg + ", speed: " + speed + ")");
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#setWheelSpeeds(int, int)
     */
    @Override
    public void setWheelSpeeds(int leftWheelSpeed, int rightWheelSpeed) {
        System.out.println("Set Wheel Speeds: " + leftWheelSpeed + ", "
                + rightWheelSpeed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#getMaximumWheelSpeed()
     */
    @Override
    public int getMaximumWheelSpeed() {
        return 720;
    }

    /*
     * (non-Javadoc)
     * 
     * @see balle.brick.Controller#kick()
     */
    @Override
    public void kick() {
        System.out.println("Kick");
    }

	@Override
	public void penaltyKick() {
		System.out.println("Penalty Kick!");
		
	}

    @Override
    public boolean isReady() {
        return true;
    }

	@Override
	public void addListener(ControllerListener cl) {
		System.out.println("Adding listener.");
	}

/*	@Override
	public void gentleKick(int speed, int angle) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void kickLeft() {
		System.out.println("Left kick");
	}
	
	@Override
	public void kickRight() {
		System.out.println("Right kick");
	}
	
	@Override
	public void kickSides() {
		System.out.println("Kick sides");
	}
	
	@Override
	public void dribblersOn() {
		System.out.println("Dribblers on");
	}
	
	@Override
	public void dribblersOff() {
		System.out.println("Dribblers off");
	}
	
	@Override
	public void extendBoth() {
		System.out.println("Extend both tentacles");
	}
	
	@Override
	public void extendLeft() {
		System.out.println("Extend the left tentacle");
	}
	
	@Override
	public void extendRight() {
		System.out.println("Extend the right tentacle");
	}
	
	@Override
	public void retractBoth() {
		System.out.println("Retract both tentacles");
	}
	
	@Override
	public void retractLeft() {
		System.out.println("Retract the left tentacle");
	}
	
	@Override
	public void retractRight() {
		System.out.println("Retract the right tentacle");
	}
	
	@Override
	public void playVerdi(){
		System.out.println("Play Verdi's Requiem");
	}

}
