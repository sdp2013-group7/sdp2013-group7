package balle.strategy.planner;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Pitch;
import balle.world.objects.Robot;

public class Intercept extends AbstractPlanner {

	private static Logger LOG = Logger.getLogger(DribbleAndScore.class);
	// Strategy
	private Strategy interceptStrategy;

	private boolean done = false;

	// Our robot and ball
	private Robot ourRobot;
	private Ball ball;
	
	private Coord robotPosition;
	
	// Positions of our robot and ball
	private Coord ballPosition;

	// Start position of the ball
	private Coord originalBallPosition;

	



	// Pitch
	private Line[] pitchSides = new Line[4];

	public Intercept() {

		interceptStrategy = new GoToBallSafeProportional();

		double pitchWidth = 1.22;
		double pitchLength = 2.44;
		pitchSides[0] = new Line(0, 0, 0, pitchWidth);
		pitchSides[1] = new Line(pitchLength, 0, pitchLength, pitchWidth);
		pitchSides[2] = new Line(0, pitchWidth, pitchLength, pitchWidth);
		pitchSides[3] = new Line(0, 0, pitchLength, 0);

	}

	// method to stop strategies that would not usually stop themselves
	@Override
	public void stop(Controller controller) {
		interceptStrategy.stop(controller);
	}

	@Override
	protected void onStep(Controller controller, Snapshot snapshot)
			throws ConfusedException {

		if (done) {
			return;
		}
		
		Pitch pitch = snapshot.getPitch();
		Robot opponent = snapshot.getOpponent();
		ourRobot = snapshot.getBalle();
		ball = snapshot.getBall();
		
		// Get positions of our robot and ball
		robotPosition = ourRobot.getPosition();
		ballPosition = ball.getPosition();

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}
		
		if (opponent.getPosition() == null) {
			LOG.info("Where is enemy? :(");
			return;
		}
		
		// Check if ball is actually on the pitch
		if (ball.getPosition() == null) {
			LOG.info("Where is ball? :(");
			return;
		}

		if (opponent.getPosition().x >= 1.2){

			Line pitchMidLine = pitch.getBottomWall();
			Coord target = opponent.getFacingLine().getIntersect(pitchMidLine);
	
			if (target == null){
				LOG.info("target is null");
				return;
			}
			
			Line targetLine = new Line (target, ourRobot.getPosition());
			
			Orientation targetOrientation = targetLine.angle();



			double orientationToturn = ourRobot.getAngleToTurn(targetOrientation);

			LOG.info("rotating " +orientationToturn);
		
			controller.rotate(-(int)Math.toDegrees(orientationToturn), 15);

			if (Math.abs(ourRobot.getOrientation().degrees() - targetOrientation.degrees()) <5)
				controller.setWheelSpeeds(50, 50);
		}		
	}
		
		
// COMPUTE POINT WHERE BALL WILL STOP. 
// DON'T NEED FOR THE MOMENT
//	
//
//	if (originalBallPosition == null) {
//	originalBallPosition = ballPosition;
//	return;
//}
//
//if (ballPosition.dist(originalBallPosition) < 0.02) 
//	return;
//		if (true) {
//			if (lastPosition == null) {
//				lastPosition = ballPosition;
//				lastTime = System.currentTimeMillis();
//				return;
//			}
//			
//			double currentSpeed = Math.abs(ballPosition.dist(lastPosition))/Math.abs(System.currentTimeMillis() - lastTime);
//			
//			if (lastSpeed == -1) {
//				lastSpeed = currentSpeed;
//				return;
//			}
//			
//			if (currentSpeed > lastSpeed) {
//				lastPosition = ballPosition;
//				lastSpeed = currentSpeed;
//				return;
//			}
//			
//			// Predict how much time will pass before ball stops
//			double travellingTime = currentSpeed/decelerationConstant;
//			
//			// Predict how far the ball will travel
//			double ballTravelDistance = decelerationConstant * Math.pow(travellingTime, 2)/2;
//			
//			double lineSlope = (originalBallPosition.y - ballPosition.y)/(originalBallPosition.x - ballPosition.x);
//			
//			double futureX = ballPosition.x;
//			double deltaX = Math.abs(ballTravelDistance*Math.cos((Math.atan(lineSlope))));
//			if (ballPosition.x >= originalBallPosition.x)
//				futureX += deltaX;
//			else 
//				futureX -= deltaX;
//			
//			double futureY = ballPosition.y;
//			double deltaY = Math.abs(ballTravelDistance*Math.sin((Math.atan(lineSlope))));
//			if (ballPosition.y >= originalBallPosition.y)
//				futureY += deltaY;
//			else 
//				futureY -= deltaY;
//			
//			if (ballStopPoint == null) {
//				ballStopPoint = new Coord(futureX, futureY);
//			}
//			
//
//			
//		
//		if (ballStopPoint != null)
//			addDrawable(new Dot(ballStopPoint, Color.BLACK));


	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Intercept", parameterNames = {})
	public static Intercept factoryMethod() {
		return new Intercept();
	}
}