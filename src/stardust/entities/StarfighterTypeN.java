package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class StarfighterTypeN extends Starcraft{
	
	public int points() {
		return 25;
	}
	
	public StarfighterTypeN(StardustGame game, double x, double y) {
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
    	}
		if (distanceTo(target)<280){
    		cooldown+=dt;
    		if(cooldown>1.25){
    			game.$currentState().addEntity(new StarcraftProjectile(game, t-0.025, this));
    			game.$currentState().addEntity(new StarcraftProjectile(game, t+0.025, this));
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
	
	// x1, y1, x2, y2 line render
	private double l[]={
		-8,2,-3,1,-3,1,-2,-3,-2,-3,-2,2,-2,2,-1,0,-1,0,1,0,1,0,2,2,2,2,2,-3,2,-3,3,1,3,1,8,2,8,2,8,-4,8,-4,10,0,10,0,10,6,10,6,8,10,8,10,8,4,8,4,3,5,3,5,3,6,3,6,2,5,2,5,0,6,0,6,-2,5,-2,5,-3,6,-3,6,-3,5,-3,5,-8,4,-8,4,-8,10,-8,10,-10,6,-10,6,-10,0,-10,0,-8,-4,-8,-4,-8,2,
	};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y-2), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		// hard code requires flip
		GL11.glRotatef(180, 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public void onDeath() {
		
	}
}
