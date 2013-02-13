package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Robot;

public class Drool extends AbstractPlanner {

	private static final Logger LOG = Logger.getLogger(Drool.class);

	private static int dist = 0;
	private static final double distToTravel = 425;
	Coord temp=null;
	private boolean done = false;
	private boolean hasLostBall= false;

	// create strategies to use
	Strategy goToBallSafeStrategy;

	public Drool() {
		
		// initialise strategies that will be used in milestone2
		goToBallSafeStrategy = new GoToBallSafeProportional();
	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		goToBallSafeStrategy.stop(controller);
	}

	@Override
	public void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (done) {
			dist = 0;
			LOG.info("finale!");
			return;
		}

		// get position of our robot
		Robot ourRobot = snapshot.getBalle();

		// check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("where am i?!?!?!");
			return;
		}
		
		// get position of the ball
		Ball ball = snapshot.getBall();
		
		LOG.info(ball.getPosition());
		
		if (ball.getPosition() != temp  && !hasLostBall){
		
		LOG.info("going to ball and pushing");
		// execute strategy to go to the ball
		goToBallSafeStrategy.step(controller, snapshot);
		// add visuals to camera stream
		addDrawables(goToBallSafeStrategy.getDrawables());

		} else {
			
			hasLostBall = true;
			LOG.info("WE LOST THE BALL :(");
			
			controller.setWheelSpeeds(50,50);
			dist++;
			
			if (dist >= distToTravel) {
				LOG.info("DONE!!!!!! Distance: " + dist);
				controller.stop();
				done = true;
			}
		}

		// when we have possession
		if (ourRobot.possessesBall(ball)&& !hasLostBall) {
			temp=ball.getPosition();
			dist++;
			LOG.info("Posess ball");
			LOG.info("dist is " + dist);

			// when distance driven reaches distance to travel then stop
			if (dist >= distToTravel) {
				LOG.info("DONE!!!!!! Distance: " + dist);
				controller.stop();
				done = true;
			}
		}			
		
	}

	// Factory method to make DribbleMilestone2 appear in the list of strats in
	// the simulator
	@FactoryMethod(designator = "Drool", parameterNames = {})
	public static Drool factoryMethod() {

		return new Drool();
	}

}
