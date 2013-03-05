package balle.strategy.planner;

import java.awt.Color;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.main.drawable.DrawableRectangularObject;
import balle.main.drawable.Dot;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Snapshot;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class AvoidMilestone3 extends AbstractPlanner {

	
	private static Logger LOG = Logger.getLogger(DribbleAndScore.class);
	private Robot ourRobot;
	private Robot enemyRobot;
	private Strategy goToBallSafeStrategy;
	private boolean turning = false;
	private int orientationToReach;

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

		// Get positions of our robot and opponent
		ourRobot = snapshot.getBalle();
		enemyRobot = snapshot.getOpponent();

		// Check if robot is actually on the pitch
		if (ourRobot.getPosition() == null) {
			LOG.info("Where am I? :(");
			return;
		}
		if (enemyRobot.getPosition() == null) {
			LOG.info("Where is enemy? :(");
			return;
		}
		
		// Get facing rectangle
		Line faceLine = ourRobot.getFacingLine();
		RectangularObject faceRect = faceLine.widen(0.3);
		
		Line leftSide = faceRect.getLeftSide();
		Line rightSide = faceRect.getRightSide();
		Line frontSide = faceRect.getFrontSide();
		Line backSide = faceRect.getBackSide();
		
		Coord corner1 = leftSide.getIntersect(frontSide);
		Coord corner2 = leftSide.getIntersect(backSide);
		Coord corner3 = rightSide.getIntersect(frontSide);
		Coord corner4 = rightSide.getIntersect(backSide);
		
//		LOG.info("Corner 1: (" + corner1.x  + " , " + corner1.y + ")");
//		LOG.info("Corner 2: (" + corner2.x  + " , " + corner2.y + ")");
//		LOG.info("Corner 3: (" + corner3.x  + " , " + corner3.y + ")");
//		LOG.info("Corner 4: (" + corner4.x  + " , " + corner4.y + ")");
		
		
		
		LOG.info("Line contains" + contains(ourRobot, faceRect, enemyRobot.getPosition()));
		
		// Check if opponent is in our way
		boolean opponentInFront = contains(ourRobot, faceRect, enemyRobot.getPosition());
		
		// Move if opponent is not in front of us
		if (!opponentInFront)
		//	controller.setWheelSpeeds(300, 300);
			controller.stop();
			
		// Otherwise stop
		else
			if (!turning) {
				controller.stop();
				double angleToTurn = 90.0;
				orientationToReach = (int)(ourRobot.getOrientation().degrees() + angleToTurn) % 365;
				//controller.rotate(90, 50);
				turning = true;
			}
			else {

			}
			
		
		addDrawable(new DrawableRectangularObject(faceRect, Color.MAGENTA));
		
		
		
		
		
		//LOG.info("opponentInWay: "+ opponentInWay(ourRobot.getPosition(), enemyRobot.getPosition(), ourRobot.getOrientation().degrees()));
		//LOG.info(ourRobot.getPosition() + ", "+enemyRobot.getPosition()+", "+ourRobot.getOrientation().degrees() );
		
		
//		enemyRobot.getPosition();
		
		//LOG.info(" Opponent in way: "+(opponentInWay(ourRobot.getPosition(), enemyRobot.getPosition(), ourRobot.getOrientation().degrees())));
		
//		while (!(opponentInWay(ourRobot.getPosition(), enemyRobot.getPosition(), ourRobot.getOrientation().degrees()))){
//			
//			
//			
//		}
	
		
		//LOG.info("Height: " + areaLine.getHeight() + "Width: " + areaLine.getWidth());
		//LOG.info("Rect pos: " + areaLine.getPosition() + "Robot pos: " + ourRobot.getPosition());

		
		
//		if (ourRobot.getPosition().y < 0.70 ){
//			
//			double ourOrientation = ourRobot.getOrientation().degrees();
//			double targetOrientation = ourOrientation - 90;
//			
//			
//			if (targetOrientation < 0){
//				
//				LOG.info("orientation altered");
//				targetOrientation= 365 -(90 - ourOrientation);
//			}
//			
//			if(ourRobot.getOrientation().degrees()!= targetOrientation){
//				
//			controller.stop();
//			LOG.info("rotate right");
//			controller.rotate(-90, 50);
//			}
			
			
//			if (targetOrientation < 0){
//				
//				targetOrientation= 365 -(90 - ourOrientation);
//				
//				while ((ourRobot.getOrientation().degrees() < 90) || (ourRobot.getOrientation().degrees() > targetOrientation)){
//					System.out.println(ourRobot.getOrientation().degrees());
//					
//				}
//				
//			}
//			
//			else {
//				
//				while (ourRobot.getOrientation().degrees() > targetOrientation){
//					System.out.println(ourRobot.getOrientation().degrees());
//				}
//				
//			}
			
			
			
			//controller.setWheelSpeeds(300, 300);
			
//		}
//		
//		else {
//			controller.stop();
//			LOG.info("rotate left");
//			controller.rotate(-90, 30);
//			//controller.setWheelSpeeds(300, 300);
//			
//		}
		
//		else if ((ourRobot.getPosition().y - 0.076) < 0.05) {// robot next to bottom wall
//			
//			
//			if (goalOrientation > 200 && goalOrientation < 160){
//				
//				
//			}
//			else if (ourRobot.isFacingLeft()){
//				
//				
//			}
//			else if (ourRobot.isFacingRight()){
//				
//				
//			}
//			
//
//		} else if ((1.15 - ourRobot.getPosition().y) < 0.05) {// robot next to top wall
//			
//		
//			
//			if (ourRobot.isFacingGoal(ourGoal)){
//				
//				
//			}
//			else if (ourRobot.isFacingGoal(opponentGoal)){
//				
//				
//			}
//			else{getHeight()
//				
//				System.out.print
//			}
//		}
		
		
			
	}
	
	
	public static boolean opponentInWay(Coord ourRobot, Coord enemyRobot, double orientationOfOurRobot){
		
		
		if (orientationOfOurRobot < 45 || orientationOfOurRobot > 315){
			
			if((ourRobot.x > enemyRobot.x)||((enemyRobot.x - ourRobot.x) > 0.50)){
				
				
				return false;
			}
			
			else if((Math.abs(ourRobot.y - enemyRobot.y))> 0.3 ){
				
				
				return false;
				
			}
			else {
				
				return true;
			}
			
			
		}
		
		else if (orientationOfOurRobot < 135){
			
			
		}
		else if (orientationOfOurRobot < 225){
	
			if((ourRobot.x < enemyRobot.x)||((ourRobot.x - enemyRobot.x) > 30)){
				
				return false;
			}
			
			else if((Math.abs(ourRobot.y - enemyRobot.y))< 22 ){
				
				return false;
				
			}
			else {
				
				return true;
			}
			
			
		}
		else {
	
	
		}
		
		
		
		
		return true;
	}
	
	public static boolean overOrUnderLine(Coord p1, Coord p2, Coord ptest) {
		double p1x = p1.x;
		double p1y = p1.y;
		double p2x = p2.x;
		double p2y = p2.y;
		
		double a = -(p2y - p1y);
		double b = p2x - p1x;
		double c = -(a*p1x + b*p1y);
		
		

		
		
		
		return (a*ptest.x + b*ptest.y + c) >= 0;
		
	}
	
	public static boolean contains(Robot ourRobot, RectangularObject areaLine, Coord point) {
		
		Line leftSide = areaLine.getLeftSide();
		Line rightSide = areaLine.getRightSide();
		Line frontSide = areaLine.getFrontSide();
		Line backSide = areaLine.getBackSide();
		
		Coord corner1 = leftSide.getIntersect(frontSide);
		Coord corner2 = leftSide.getIntersect(backSide);
		Coord corner3 = rightSide.getIntersect(frontSide);
		Coord corner4 = rightSide.getIntersect(backSide);
		
		
//		
//		double xPoint = point.getX();
//		double yPoint = point.getY();
		
		
		
		boolean betweenLR = overOrUnderLine(corner1, corner2, point) != overOrUnderLine(corner3, corner4, point);
		boolean betweenFR = overOrUnderLine(corner1, corner3, point) != overOrUnderLine(corner2, corner4, point);
		
		return betweenLR && betweenFR;
		
//		double rectHeight = areaLine.getHeight();
//		double rectWidth = areaLine.getWidth();
//		
//		double maxY = ourRobot.getPosition().y + rectWidth/2.0;
//		double minY = ourRobot.getPosition().y - rectWidth/2.0;
//		
//		double maxX = ourRobot.getPosition().x + 0.6;
//		double minX = ourRobot.getPosition().x;
//		
//		if ((yPoint > minY && yPoint < maxY) && (xPoint > minX && xPoint < maxX))
//			return true;
//		
//		return false;
	}
	

	// To make it a usable stand-alone strategy
	@FactoryMethod(designator = "Avoid Milestone 3", parameterNames = {})
	public static AvoidMilestone3 factoryMethod() {
		return new AvoidMilestone3();
	}
}
