package balle.strategy;

import org.apache.log4j.Logger;
import balle.controller.Controller;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.GoToBallSafeProportional;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Robot;

/* A strategy to get the ball away from the walls using the dribblers.
 * 
 * Author: Georgi Tsatsev - s1045049
 
 */


public class NewDribble extends AbstractPlanner {
	
	private Logger LOG = Logger.getLogger(NewDribble.class);
	private Robot ourRobot;
	private Ball ball;
	private Goal goal;
	private Strategy goToBallSafeStrategy;
	private boolean done = false;
    

	public NewDribble() {
		goToBallSafeStrategy = new GoToBallSafeProportional();
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		controller.dribblersOff();
		goToBallSafeStrategy.stop(controller);
	}
	
	@Override
	protected void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {
		
		
		ourRobot = snapshot.getBalle();
		ball = snapshot.getBall();
		goal = snapshot.getOpponentsGoal();
		
		// Check if the strategy is finished.
		if (done) {
			LOG.info("We got the ball away from the wall.");
			controller.dribblersOff();
			controller.stop();
			return;
		}

		// Check if the robot is on the pitch.
		if (ourRobot.getPosition() == null) {
			LOG.info("No robot is present on the pitch");
			return;
		}
		
		// If we dont have possession  of the ball go for it.
		if (!ourRobot.possessesBall(ball)){
			LOG.info("We has no ball. I shall get it.");
			goToBallSafeStrategy.step(controller, snapshot);
			// Adds the point of positioning of our robot while 
			//executing the goToBallSafeStrategy.
			addDrawables(goToBallSafeStrategy.getDrawables());
		}
		// Else turn the "dribblers" on and go backwards and we are done.
		else {
			controller.dribblersOn();
			LOG.info("The dribblers have been turned on");
			
			// Condition for turning when the goal is left or right
			if (goal.isLeftGoal()){
				
				// Condition for our robot being in the upper part or the lower part of the pitch 
				if (ourRobot.getPosition().getY()<0.6){
					LOG.info("The goal is left and we are down.");
	    			controller.setWheelSpeeds(-35,-70);
				} else {
					LOG.info("The goal is left and we are up");
	    			controller.setWheelSpeeds(-70,-35);
				}
			} else{
				
				// Condition for our robot being in the upper part or the lower part of the pitch 
				if (ourRobot.getPosition().getY()<0.6){
					LOG.info("The goal is right and we are down.");
	    			controller.setWheelSpeeds(-70,-35);
				} else {
					LOG.info("The goal is right and we are up");
	    			controller.setWheelSpeeds(-35,-70);
				}
			}
			
			// We are executing the turn until we are facing the goalpost.
			if (ourRobot.isFacingGoal(goal)){
				done=true;
			}
		}
		
	}
	
	// FactoryMethod is used to make the strategy appear in the simulator drop-down menu.
	@FactoryMethod(designator = "NewDribble", parameterNames = {})
	public static NewDribble factoryMethod()
	{
		return new NewDribble();
	} 
}