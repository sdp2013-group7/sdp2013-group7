package balle.strategy.planner;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import balle.strategy.bezierNav.BezierNav;
import balle.strategy.curve.CustomCHI;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.executor.movement.MovementExecutor;
import balle.strategy.executor.movement.OrientedMovementExecutor;
import balle.strategy.pathFinding.ForwardAndReversePathFinder;
import balle.strategy.pathFinding.PathFinder;
import balle.strategy.pathFinding.SimplePathFinder;
import balle.controller.Controller;
import balle.main.drawable.DrawableLine;
import balle.main.drawable.DrawableRectangularObject;
import balle.strategy.ConfusedException;
import balle.strategy.FactoryMethod;
import balle.world.Coord;
import balle.world.Line;
import balle.world.Orientation;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.Pitch;
import balle.world.objects.Point;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class CurvyIntercept extends AbstractPlanner {
	
	private boolean hasMoved = false;
	private ArrayList<Coord> ballbuff = new ArrayList<>();
	private MovementExecutor movementExecutor;
	private OrientedMovementExecutor orientedMovementExecutor;
	private static Logger LOG  = Logger.getLogger(CurvyIntercept.class);
	private boolean done = false;
	/*
    public CurvyIntercept(MovementExecutor movementExecutor,OrientedMovementExecutor orientedMovementExecutor) {
    	this.movementExecutor = movementExecutor;
        this.orientedMovementExecutor = orientedMovementExecutor;
    }
    */
/*
    @FactoryMethod(designator = "Curvy Intercept Strategy", parameterNames = {})
    public static final CurvyIntercept factoryNCPBZR() {
		return new CurvyIntercept (null, new BezierNav(
				new SimplePathFinder(
				new CustomCHI())));
    }
    */
	
	 public CurvyIntercept(MovementExecutor movementExecutor) {
	    	this.movementExecutor = movementExecutor;
	        
	    }
	   
    @FactoryMethod(designator = "Curvy Intercept Strategy", parameterNames = {})
    public static final CurvyIntercept factoryMethod() {
		return new CurvyIntercept ( new GoToObjectPFN(0));
    }
    
    protected void setIAmDoing(String message) {
        LOG.info(message);
    }
    
    // Return true if the ball has moved at all since the strategy started.
    public boolean hasBallMoved(){
    	int n = ballbuff.size();
    	if (n>=2)
    		hasMoved = hasMoved||ballbuff.get(n-1).dist(ballbuff.get(n-2))>0.005;
    	   		   	
    	return hasMoved;
    	
    }
   
    @Override
    public void stop(Controller controller) {
    	movementExecutor.stop(controller);
    	controller.kick();
    	LOG.info("Just so you know I just kicked your ass!");
    	controller.stop();

    }

    
    
    ///////////////////////////////////////ON STEP///////////////////////////////////
		
	
	
	@Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

		// Variables Used 
		Ball ball = snapshot.getBall();
		Pitch pitch = snapshot.getPitch();
		Robot opponent = snapshot.getOpponent();
		Robot ourRobot = snapshot.getBalle();		
		
		Line pitchLine;
		RectangularObject goalRect;
		double distance = 0;
		Coord target;
		double angle;
		Line interceptLine;
		int n;
		
		if (done){
			stop(controller);
		}
		// Dummy 
		if (ball.getPosition() == null)
			return;
		if (ourRobot.getPosition() == null) 
			return;
		if (opponent.getPosition() == null) 
			return;
		
		// Ball buffer.
		ballbuff.add(ball.getPosition());
		n = ballbuff.size();
		angle = ourRobot.getAngleToTurnToTarget(ball.getPosition());
		angle = Math.toDegrees(angle) / 2;
		
		// Check for opponent position. If it is on left side then we need to intercept
		//towards right. Else towards left.
		if (opponent.getPosition().getX() >= 1.2){
			pitchLine = pitch.getBottomWall();
			goalRect = pitchLine.widen(0.5);
			pitchLine = goalRect.getRightSide();
					
		}else {
			pitchLine = pitch.getTopWall();
			goalRect = pitchLine.widen(0.5);
			pitchLine = goalRect.getLeftSide();
				
		}
		
		// Draw the rectangular object that is used for the intercept.	
		addDrawable(new DrawableRectangularObject(goalRect, Color.MAGENTA));
		
		
		
		
		// If ball has moved intercept
		if (hasBallMoved()){
			
			interceptLine=(new Line (ballbuff.get(n-2),ballbuff.get(n-1)));
			interceptLine=interceptLine.extendBothDirections(2);
			addDrawable(new DrawableLine(interceptLine, Color.MAGENTA));
		
			if (interceptLine.intersects(pitchLine)){
				
				setIAmDoing("Intercepting!");
				addDrawable(new DrawableLine(interceptLine, Color.MAGENTA));
				target = interceptLine.getIntersect(pitchLine);
				
				
				if (ourRobot.getPosition().dist(target)<0.1){
					setIAmDoing("Adjusting towards ball!");
					
					if (angle > 10 || angle < -10) {
						controller.dribblersOn();
						LOG.info("Dribblers on");
						controller.rotate((int) angle, 50);
						LOG.info("Angle: " + (int) angle);
					} else {
						stop(controller);
					}
					
				}else{
					if (movementExecutor != null) {
						movementExecutor.updateTarget(new Point(target));
						addDrawables(movementExecutor.getDrawables());
						movementExecutor.step(controller, snapshot);
					}
				}
					
					
				
			} else {
				setIAmDoing("Interception Point outside of pitch!");
				stop(controller);
			}
			
		} else {
			 setIAmDoing("Waiting!");
		}
	} 


}


