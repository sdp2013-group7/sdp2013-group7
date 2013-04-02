package balle.strategy;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.DribbleMilestone2;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Robot;

public class dribblerz extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(DribbleMilestone2.class);
	
    @Override
    protected void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {
    	    	
    	// Robot variable holding the position of the robot.
    	Robot ourRobot = snapshot.getBalle();    	
    	// Ball variable holding the position of the ball.
    	Ball ball = snapshot.getBall();
    	
    	/* Check if we posses the ball or cant see it then turn the dribblers on
    	
    	*/
    	if (ourRobot.possessesBall(ball) || ball.getPosition()==null) {
    		
    		controller.dribblersOn();
    		LOG.info("The dribblers are on");
    		
    	} else {
    		
    		controller.dribblersOff();
    		LOG.info("The dribblers are off");
    		
    	}
    	
    }


    //@FactoryMethod(designator = "Simple Dribblers", parameterNames = {})
    public static final dribblerz factory() {
        return new dribblerz();
    }

}
