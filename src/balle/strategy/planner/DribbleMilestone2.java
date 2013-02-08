package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.executor.turning.IncFaceAngle;
import balle.strategy.executor.turning.RotateToOrientationExecutor;
import balle.strategy.planner.AbstractPlanner;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Robot;

public class DribbleMilestone2 extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(DribbleMilestone2.class);

	// create strategies to use
	Strategy goToBallSafeStrategy;

	public DribbleMilestone2() {

		// initialise strategies that will be used in milestone2
		goToBallSafeStrategy = new GoToBallNoGoals(new GoToObjectPFN(0.15f));
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		
		goToBallSafeStrategy.stop(controller);
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

		// get position of our robot
		Robot ourRobot = snapshot.getBalle();

		// check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			
		LOG.info("where am i?!?!?!");
		return;
		}

		// get position of the ball
		Ball ball = snapshot.getBall();
		

		//when we have possession
		if (ourRobot.possessesBall(ball)) {
			
			LOG.info("we have possession");
			controller.stop();
		} 
		
		// if robot is not near ball
		 else {

			LOG.info("going to ball");
			
			// execute strategy to go to the ball
			goToBallSafeStrategy.step(controller, snapshot);
			// add visuals to camera stream
			addDrawables(goToBallSafeStrategy.getDrawables());
		}
	}

	// Factory method to make DribbleMilestone2 appear in the list of strats in the simulator
	@FactoryMethod(designator = "DribbleMilestone2", parameterNames = {})
	public static DribbleMilestone2 factoryMethod() {
		
		return new DribbleMilestone2();
	}
}
