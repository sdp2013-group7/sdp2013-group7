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

public class AvoidMilestone3 extends AbstractPlanner {

	
	private Logger LOG = Logger.getLogger(DribbleAndScore.class);
	private Robot ourRobot;
	private Ball ball;
	private Goal ourGoal;
	private Goal opponentGoal;
	private Robot enemyRobot;
	private Strategy goToBallSafeStrategy;

	public AvoidMilestone3() {
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
		enemyRobot = snapshot.getOpponent();
		ourGoal = snapshot.getOwnGoal();
		opponentGoal = snapshot.getOpponentsGoal();

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}
		
		controller.setWheelSpeeds(300, 300);


		if ((ourRobot.getPosition().y - 0.076) < 0.05) {// robot next to bottom wall
			
			
			if (ourRobot.isFacingGoal(ourGoal)){
				
				
			}
			else if (ourRobot.isFacingGoal(opponentGoal)){
				
				
			}
			else{
				
				
			}
			

		} else if ((1.15 - ourRobot.getPosition().y) < 0.05) {// robot next to top wall
			
		
			
			if (ourRobot.isFacingGoal(ourGoal)){
				
				
			}
			else if (ourRobot.isFacingGoal(opponentGoal)){
				
				
			}
			else{
				
				
			}
			
			
			
			
			
			
			
		} else {
			
			LOG.info("Going to the ball");
			controller.setWheelSpeeds(300, 300);
			
			// Use the strategy to go towards the ball
			goToBallSafeStrategy.step(controller, snapshot);
			
			// "Add visuals to camera stream" personally unsure what this does
			addDrawables(goToBallSafeStrategy.getDrawables());
		}
	}

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Avoid Milestone 3", parameterNames = {})
	public static AvoidMilestone3 factoryMethod() {
		return new AvoidMilestone3();
	}
}
