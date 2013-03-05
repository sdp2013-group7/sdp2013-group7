/**
 * 
 */
package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
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
	private boolean hasBall = false;
	private boolean done = false;

	public DribbleAndScore() {
		goToBallSafeStrategy = new GoToBallSafeProportional();
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		controller.dribblersOff();
		goToBallSafeStrategy.stop(controller);
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		// Get positions or our robot, ball and goal
		ourRobot = snapshot.getBalle();
		ball = snapshot.getBall();
		goal = snapshot.getOpponentsGoal();
		
		if (done) {
			return;
		}

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}

		if (hasBall && ourRobot.canScoreNoOpposition(ball, goal)) {
			
			LOG.info("Let's score a goal! and dribblers off");
			controller.dribblersOff();
			controller.kick();
			done = true;

		} else if (ourRobot.possessesBall(ball)&& !ourRobot.canScoreNoOpposition(ball, goal) && !hasBall) {
			
			LOG.info("Moving to a better scoring location :)");
			
			// get angle we need to turn to have ball in the centre of robot
			double angle = ourRobot.getAngleToTurnToTarget(goal.getPosition());

			// convert to degrees
			angle = Math.toDegrees(angle) / 2;

			// if angle is large enough (greater than 10) then turn to face ball
			if (angle > 10 || angle < -10) {

				controller.rotate((int) angle, 50);
				LOG.info("Angle: " + (int) angle);
			}
			
			// Keep dribblers on
			controller.dribblersOn();
			LOG.info("dribblers on");
			hasBall = true;
			
			// Use the strategy to go towards the ball
			goToBallSafeStrategy.step(controller, snapshot);
			
			// "Add visuals to camera stream" personally unsure what this does
			addDrawables(goToBallSafeStrategy.getDrawables());
		} else if (hasBall && !ourRobot.canScoreNoOpposition(ball, goal)) {
			LOG.info("Move forward a bit more");
			controller.setWheelSpeeds(200, 200);
			
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
