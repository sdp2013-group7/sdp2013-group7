package balle.strategy;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import balle.controller.Controller;
import balle.main.drawable.Drawable;
import balle.main.drawable.DrawableRectangularObject;
import balle.main.drawable.Label;
import balle.misc.Globals;
import balle.strategy.executor.movement.GoToObjectPFN;
import balle.strategy.planner.AbstractPlanner;
import balle.strategy.planner.CurvyIntercept;
import balle.strategy.planner.DribbleAndScore;
import balle.world.Coord;
import balle.world.Snapshot;
import balle.world.objects.Ball;
import balle.world.objects.RectangularObject;
import balle.world.objects.Robot;

public class Milestone4 extends AbstractPlanner {

    private static final Logger LOG = Logger.getLogger(Milestone4.class);
    
	// Strategies that we will need make sure to call stop() for each of them
	
    
    protected final Strategy Intercept;
    protected final Strategy Score;
    private String currentStrategy = null;

    
    // return the strategy that is currently being run
    public String getCurrentStrategy() {
    	
        return currentStrategy;
    }




    @Override
    public ArrayList<Drawable> getDrawables() {
    	
        ArrayList<Drawable> drawables = super.getDrawables();
        
        if (currentStrategy != null){
        	
            drawables.add(new Label(currentStrategy, new Coord(
                    Globals.PITCH_WIDTH - 0.5, Globals.PITCH_HEIGHT + 0.2),
                    Color.WHITE));
        }
        
        return drawables;
    }

    // set strategy that is to be used right when this function is called
    public void setCurrentStrategy(String currentStrategy) {
    	
        this.currentStrategy = currentStrategy;
    }

    // put strategy into simulator
    //@FactoryMethod(designator = "Milestone4", parameterNames = {})
    
    public static Milestone4 factoryMethod() {
    	 Milestone4 strategy = new Milestone4();
        return strategy;
    }

    // initialise strategies that are to be used
    public Milestone4() {
    	Intercept = new CurvyIntercept (new GoToObjectPFN(0));
    	
    	Score = new DribbleAndScore();
    	
       
    }





    // stop strategies that would not normally stop themselves
    @Override
    public void stop(Controller controller) {
    	
        Score.stop(controller);
    }

    
    
    ////////////////////////////////////////////////////////////// ON STEP //////////////////////////////////////////////////////////////////////////////////
    
    
    @Override
	public void onStep(Controller controller, Snapshot snapshot) throws ConfusedException {

    	// get our robot and the ball
        Robot ourRobot = snapshot.getBalle();
        Ball ball = snapshot.getBall();

        // if neither the ball nor the robot can be found then return
        if ((ourRobot.getPosition() == null) || (ball.getPosition() == null)){
        	return;
        }
        
      

       
		Strategy strategy = getStrategy(snapshot);
        LOG.debug("Selected strategy: " + strategy.getClass().getName());
        
        // use strategy that was chosen
		setCurrentStrategy(strategy.getClass().getName());
		
		 try {
	        	
	            strategy.step(controller, snapshot);
	        
	        } catch (ConfusedException e) {
	        	
	            // If a strategy does not know what to do
				LOG.error("Wut Wut?", e);
	            // Default to goToBallPFN
	            Intercept.step(controller, snapshot);
	        }
		

	
	}

    // function to choose strategy to be used
	private Strategy getStrategy(Snapshot snapshot) {
		
		// get all objects
		Robot ourRobot = snapshot.getBalle();

      
        RectangularObject dribbleBox = ourRobot.getFrontSide().extendBothDirections(0.01).widen(0.25);
        addDrawable(new DrawableRectangularObject(dribbleBox, Color.CYAN));
        

        System.out.println((boolean)((CurvyIntercept) Intercept).isDone());
        if (!(boolean)((CurvyIntercept) Intercept).isDone()){
        	addDrawables(Intercept.getDrawables());
        	return Intercept;
        } else {
        	return Score;
        }
        
        
        

        
        

	}
}
