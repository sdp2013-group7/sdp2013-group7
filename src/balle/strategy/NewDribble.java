package balle.strategy;

import java.awt.Color;
import org.apache.log4j.Logger;
import balle.controller.Controller;
import balle.main.drawable.DrawableLine;
import balle.main.drawable.DrawableRectangularObject;
import balle.strategy.executor.movement.ModifiedGoToObjectPFN;
import balle.strategy.executor.movement.MovementExecutor;
import balle.strategy.planner.AbstractPlanner;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Point;
import balle.world.objects.RectangularObject;
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
	private boolean done = false;
	private MovementExecutor movementExecutor;
	// Boolean variable representing if we are trying to go for the ball.
	public boolean trying = true;
    

	public NewDribble(MovementExecutor movementExecutor) {
	    	this.movementExecutor = movementExecutor;   
	    }
	   
	//@FactoryMethod(designator = "New Dribble bitchez", parameterNames = {})
	public static final NewDribble factoryMethod() {
		 return new NewDribble ( new ModifiedGoToObjectPFN(0));
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		controller.stop();
	}
	
	// Check if our robot has possession of the ball. There is a white rectangle
	//that represents this possesion area.
    public boolean hasBall(Ball ball) {
        
    	if ((ball.getPosition() == null) || (ourRobot.getPosition() == null))
            return false;
        
        Line frontLine = ourRobot.getFrontSide().extendBothDirections(-0.03);
		double distance = (frontLine.dist(ball.getPosition()));
        
		// Draw possession area
        RectangularObject possessionRect = frontLine.widen(0.005);
		addDrawable(new DrawableRectangularObject (possessionRect, Color.WHITE));
		
        return distance <= 0.005;
    }
	
    // Check to see if our robot is facing the middle part of the opponents goal.
    public boolean facingGoal(Robot robot,Goal goal){
    	
    	Line goalLine = goal.getGoalLine();
    	Coord midA = (new Line (goalLine.getA(),goalLine.getCenter())).getCenter();
    	Coord midB = (new Line (goalLine.getB(),goalLine.getCenter())).getCenter();
    	Line modified=new Line (midA,midB);
    	
		addDrawable(new DrawableLine (modified, Color.WHITE));
		
    	Line facingLine = robot.getFacingLine();
    	return facingLine.intersects(goalLine);
        //return facingLine.intersects(modified);

    }
	
///////////////////////////////////////ON STEP///////////////////////////////////
	
	
	@Override
	protected void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {
		
		ourRobot = snapshot.getBalle();
		ball = snapshot.getBall();
		goal = snapshot.getOpponentsGoal();
		
		// Check if the strategy is finished.
		if (done) {
			LOG.info("We got the ball away from the wall.");
			controller.stop();
			return;
		}

		// Check if the robot is on the pitch.
		if (ourRobot.getPosition() == null) {
			LOG.info("No robot is present on the pitch");
			return;
		}
		
		// If we don't have possession of the ball go for it.
		if ((!hasBall(ball))&&trying){
			
			LOG.info("We has no ball. I shall get it.");
			// Unnecessary things.
			double newX=ball.getPosition().getX();
			double newY=ball.getPosition().getY();
			
			// Set up the target towards which we are going.
			Coord target =new Coord (newX,newY);
			movementExecutor.updateTarget(new Point(target));
			movementExecutor.step(controller, snapshot);
			addDrawables(movementExecutor.getDrawables());

		}
		// Else start moving backwards hopefully with the ball.
		else {
			trying = false;
			
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
			
			// We are executing the turn until we are facing the opponents Goal.
			if (facingGoal(ourRobot,goal)){
				controller.stop();
				if (hasBall(ball)){
					done=true;
				// If we have lost the ball in the meantime go for it.
				} else {
					trying = true;
				}
				
			}
		}
		
	}
	

}