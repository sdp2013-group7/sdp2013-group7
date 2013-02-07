package balle.strategy.friendly1;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.strategy.Strategy;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.executor.turning.IncFaceAngle;
import balle.strategy.executor.turning.RotateToOrientationExecutor;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.KickFromWall;
import balle.world.Coord;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Goal;
import balle.world.objects.Pitch;
import balle.world.objects.Robot;

public class Game extends AbstractPlanner {

    private static final Logger LOG = Logger.getLogger(Game.class);
    // Strategies that we will need make sure to updateState() for each of them
    // and stop() each of them
    Strategy defensiveStrategy;
    Strategy goToBallStrategy;
    Strategy pickBallFromWallStrategy;
    RotateToOrientationExecutor turningExecutor;

    public Game() {
    	
    	
    	//initialise strategies that will be used in game
    	
        defensiveStrategy = new DefensiveStrategy(new GoToObjectPFN(0.15f));
        // TODO: implement a new strategy that inherits from GoToBall but always
        // approaches the ball from correct angle. (This can be done by always
        // pointing robot
        // to a location that is say 0.2 m before the ball in correct direction
        // and then, once the robot reaches it, pointing it to the ball itself
        // so it reaches it.
        goToBallStrategy = new GoToBall(new GoToObjectPFN(0.15f), true);
        // TODO: UPDATE THIS
        pickBallFromWallStrategy = new KickFromWall(new GoToObjectPFN(0.15f));
        turningExecutor = new IncFaceAngle();
    }

    // method to stop strategies that would not usually stop themselves
    @Override
    public void stop(Controller controller) {
        defensiveStrategy.stop(controller);
        goToBallStrategy.stop(controller);
    }

    @Override
    public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

    	
    	//get position of our robot
    	Robot ourRobot = snapshot.getBalle();
        
    	//check if robot is actually on the pitch
        if (ourRobot.getPosition() == null){
            return;
        }
        
        //get positions of other useful objects on pitch
        Robot opponent = snapshot.getOpponent();
        Ball ball = snapshot.getBall();
        Goal ownGoal = snapshot.getOwnGoal();
        Goal opponentsGoal = snapshot.getOpponentsGoal();
        Pitch pitch = snapshot.getPitch();

        //get angle we need to turn to face the ball
        Orientation targetOrientation = ball.getPosition().sub(ourRobot.getPosition()).orientation();
        
        //when we have possession
        if (ourRobot.possessesBall(ball)) {
        	
            // Kick if we are facing opponents goal
            if (!ourRobot.isFacingGoalHalf(ownGoal)) {
            	
                LOG.info("Kicking the ball");
                controller.kick();
                
                // Slowly move forward as well in case we're not so close
                controller.setWheelSpeeds(200, 200);
                
            } else {

                // TODO: turn the robot slightly so we face away from our
                // own goal.
                // Implement a turning executor that would use
                // setWheelSpeeds to some arbitrary low
                // number (say -300,300 and 300,-300) to turn to correct
                // direction and use it here.
                // it has to be similar to FaceAngle executor but should not
                // use the controller.rotate()
                // command that is blocking.
            	
            	//get position again (in case something has changed?)
                Coord r, b, g;
                r = ourRobot.getPosition();
                b = ball.getPosition();
                g = ownGoal.getPosition();

                // if angle between is greater than to 180 then atan2styleradians() will return -1*(2*Math.pi - angle) because the previous group split it 360 into halves
                if (r.angleBetween(g, b).atan2styleradians() < 0) {
                	
                	//if negative angle we rotate clockwise
                	
                	//get maximum rotation that can be done without hitting the ball away (ball to stay within a radius of 10 from the robot)
                    Orientation orien = ourRobot.findMaxRotationMaintaintingPossession(ball, true);
                    System.out.println(orien);
                    
                    //set angle to rotate
                    turningExecutor.setTargetOrientation(orien);
                    //execute rotation
                    turningExecutor.step(controller, snapshot);
                    
                    //if ball is now within kicking range (<10 degrees) then kick the ball
                    if (ourRobot.findMaxRotationMaintaintingPossession(ball, true).degrees() < 10){
                        controller.kick();
                    }
                    
                } else {
                	
                    //angle between was positive so rotate Anti-Clockwise
                	
                	//get maximum rotation that can be done without hitting the ball away (ball to stay within a radius of 10 from the robot)
                    Orientation orien = ourRobot.findMaxRotationMaintaintingPossession(ball, false);
                    System.out.println(orien);
                    
                    //set angle to rotate
                    turningExecutor.setTargetOrientation(orien);
                    //execute rotation
                    turningExecutor.step(controller, snapshot);

                    //if ball is now within kicking range (> -10 degrees) then kick the ball
                    if (ourRobot.findMaxRotationMaintaintingPossession(ball,false).degrees() > -10)
                        controller.kick();
                }
            }
            
            
            //if opponent has possession and is facing our goal
        } else if ((opponent.possessesBall(ball))&& (opponent.isFacingGoal(ownGoal))) {
        	
            LOG.info("Defending");
            // Let defensiveStrategy deal with it!

            //execute defensive strategy
            defensiveStrategy.step(controller, snapshot);

            //show visual representation of strat on camera stream
            addDrawables(defensiveStrategy.getDrawables());
       
        
        
        
        //if ball is near the wall
        } else if (ball.isNearWall(pitch)) {

        	//execute strat to remove from wall
            pickBallFromWallStrategy.step(controller, snapshot);
            //show visual representation of strat on camera stream
            addDrawables(pickBallFromWallStrategy.getDrawables());
            
            
            
        //if robot is near the ball and we are looking at the ball from the correct direction to move towards enemy goal
        } else if (ball.isNear(ourRobot)&& (ourRobot.isApproachingTargetFromCorrectSide(ball,snapshot.getOpponentsGoal()))) {
        	
        	//if angle that we need to turn to face the ball is greater than 45 degrees
            if (Math.abs(ourRobot.getAngleToTurn(targetOrientation)) > (Math.PI / 4)) {
            	
                LOG.info("Ball is near our robot, turning to it");
                
                //set angle
                turningExecutor.setTargetOrientation(targetOrientation);
                //turn to face ball
				turningExecutor.step(controller, snapshot);

            } else {
                // otherwise go forward!
                controller.setWheelSpeeds(400, 400);
            }
            
            
        //if robot is not near ball 
        } else {
        	
            // Approach ball
        	
        	//execute strategy
            goToBallStrategy.step(controller, snapshot);
            //add visuals to camera stream
            addDrawables(goToBallStrategy.getDrawables());

        }

    }

    //Factory method to make Friendly1 appear in the list of strats in the simulator
    @FactoryMethod(designator = "Friendly1", parameterNames = {})
    public static Game factoryMethod() {
        return new Game();
    }
}

