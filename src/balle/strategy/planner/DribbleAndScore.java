/**
 * 
 */
package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Robot;

/**
 * @author s1039549
 * 
 */
public class DribbleAndScore extends AbstractPlanner {

	private Logger LOG = Logger.getLogger(DribbleAndScore.class);
	private Robot ourRobot;
	private Ball ball;
	private Goal goal;
	private Strategy goToBallSafeStrategy;

	public DribbleAndScore() {
		goToBallSafeStrategy = new GoToBallSafeProportional();
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		goToBallSafeStrategy.stop(controller);
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		// Get positions or our robot, ball and goal
		ourRobot = snapshot.getBalle();
		ball = snapshot.getBall();
		goal = snapshot.getOpponentsGoal();

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}

		if (ourRobot.possessesBall(ball)
				&& ourRobot.canScoreNoOpposition(ball, goal)) {
			LOG.info("Let's score a goal!");
			controller.dribblersOff();
			controller.kick();

		} else if (ourRobot.possessesBall(ball)
				&& !ourRobot.canScoreNoOpposition(ball, goal)) {
			
			LOG.info("Moving to a better scoring location :)");
			
			// Keep dribblers on
			controller.dribblersOn();
			
			// Use the strategy to go towards the ball
			goToBallSafeStrategy.step(controller, snapshot);
			
			// "Add visuals to camera stream" personally unsure what this does
			addDrawables(goToBallSafeStrategy.getDrawables());
		} else {
			
			LOG.info("Going to the ball");
			
			// Use the strategy to go towards the ball
			goToBallSafeStrategy.step(controller, snapshot);
			
			// "Add visuals to camera stream" personally unsure what this does
			addDrawables(goToBallSafeStrategy.getDrawables());
		}

	}

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Dribble and Score", parameterNames = {})
	public static DribbleAndScore factoryMethod() {
		return new DribbleAndScore();
	}
}
