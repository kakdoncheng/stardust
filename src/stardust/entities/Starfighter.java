package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class Starfighter extends Starcraft{
	
	public int points() {
		return 20;
	}
	
	public Starfighter(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
	}
	
	private double aF=250;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// face target
		t=directionTo(target);
		
		// move towards target & weapons
		boolean isMoving=false;
		if (distanceTo(target)>192) {
			applyAccelerationVector(t, aF, dt);
			isMoving=true;
    	}else{
    		cooldown+=dt;
    		if(cooldown>1.0){
    			game.$currentState().addEntity(new StarcraftProjectile(game, t, this));
    			cooldown=0;
    		}
    	}
		
		// friction brakes
		double cs=$speed();
		if(cs>(isMoving?0:8)){
			double ft=Vector.constrainTheta(Vector.dxyToDirection(dx, dy)+Math.PI);
			double fF=isMoving?cs*cs*0.005:aF;
			applyAccelerationVector(ft, fF, dt);
		}else{
			dx=0;
			dy=0;
		}
		
		// resolve movement
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}
	
	private double scale=2;
	//private double[] cx={ 0, 1, 2, 5, 5, 2, 1, 1, 0,-1,-1,-2,-5,-5,-2,-1};
	//private double[] cy={-2,-1,-2,-1, 0, 2, 5, 1, 3, 1, 5, 2, 0,-1,-2,-1};
	private double[] cx={ 0, 1, 1, 2, 4, 4, 2, 1, 1, 0,-1,-1,-2,-4,-4,-2,-1,-1};
	private double[] cy={-2,-1,-3,-1,-1, 0, 1, 2, 1, 5, 1, 2, 1, 0,-1,-1,-3,-1};
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);	
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0,0,alpha);
		setRadarColor(1);
		for(int i=0; i<cx.length; i++){
			GL11.glVertex2d(cx[i]*c.$zoom()*scale, cy[i]*c.$zoom()*scale);
			GL11.glVertex2d(cx[(i+1)%cx.length]*c.$zoom()*scale, cy[(i+1)%cx.length]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//this.renderCollisionBounds(c, 8);
	}
	
	public void onDeath() {
		
	}
}
