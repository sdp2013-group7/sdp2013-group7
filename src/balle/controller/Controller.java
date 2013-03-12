package balle.controller;

public interface Controller {

    /**
     * Returns if controller is ready to operate
     * 
     * @return true if controller is ready to operate
     */
    public abstract boolean isReady();

    /**
     * Makes the robot go backward with the specified speed
     * 
     * @param speed
     */
    public abstract void backward(int speed);

    /**
     * Makes robot go forward with the specified speed
     * 
     * @param speed
     */
    public abstract void forward(int speed);

    /**
     * Floats the motors, allowing the robot to drift.
     */
    public abstract void floatWheels();

    /**
     * Makes the robot stop.
     */
    public abstract void stop();

    /**
     * The robot will rotate deg degrees at specified speed.
     * 
     * @param deg
     *            The degree to rotate. +ve is right, -ve left.
     * @param turn
     *            speed
     */
    public abstract void rotate(int deg, int speed);

    /**
     * Sets the speeds of individual wheels in the robot
     * 
     * @param leftWheelSpeed
     *            left wheel speed
     * @param rightWheelSpeed
     *            right wheel speed
     */
    public abstract void setWheelSpeeds(int leftWheelSpeed, int rightWheelSpeed);

    /**
     * Returns the maximum supported wheel speed that can be used by
     * setWheelSpeeds
     * 
     * @return maximum speed
     */
    public abstract int getMaximumWheelSpeed();

    /**
     * Kicks the ball.
     */
    public abstract void kick();

    /**
     * Penalty kick
     */
    public abstract void penaltyKick();
    
    /**
     * 	Kick with the left tentacle
     */
    public abstract void kickLeft();
    
    /**
     * Kick with the right tentacle
     */
    public abstract void kickRight();
    
    /**
     * Kick with both tentacles
     */
    public abstract void kickSides();
    
    /**
     * Turn on the dribblers
     */
    public abstract void dribblersOn();
    
    /**
     * Turn off the dribblers
     */
    public abstract void dribblersOff();
    
    
    /*
     * The methods for extending the tentacles
     */
    public abstract void extendBoth();
    public abstract void extendLeft();
    public abstract void extendRight();
    
    /*
     * The methods for retracting the tentacles
     */
    public abstract void retractBoth();
    public abstract void retractLeft();
    public abstract void retractRight();
    
    /**
     * Play Verdi's Requiem
     */
    public abstract void playVerdi();
    
    //public abstract void gentleKick(int speed, int angle);

	public abstract void addListener(ControllerListener cl);
}