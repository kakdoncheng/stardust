package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class StarfighterTypeC extends Starcraft{
	
	public int points() {
		return 30;
	}
	
	public StarfighterTypeC(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
	}
	
	private double aF=275;
	private double cooldown=1;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// face target
		t=directionTo(target);
		
		// move towards target & weapons
		boolean isMoving=false;
		if (distanceTo(target)>200) {
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
	
	// x1, y1, x2, y2 line render
	private double l[]={
		//-2,1,-2,-1,-2,-1,-3,1,-3,1,-8,2,-8,2,-8,1,-8,1,-9,4,-9,4,-3,5,-3,5,-3,6,-3,6,-2,7,-2,7,-1,6,-1,6,-1,5,-1,5,0,6,0,6,1,5,1,5,1,6,1,6,2,7,2,7,3,6,3,6,3,5,3,5,9,4,9,4,8,1,8,1,8,2,8,2,3,1,3,1,2,-1,2,-1,2,1,2,1,0,-6,0,-6,-2,1,
			0,-6,2,1,2,1,2,-2,2,-2,3,0,3,0,7,2,7,2,7,1,7,1,8,3,8,3,6,5,6,5,6,4,6,4,3,5,3,5,2,6,2,6,1,5,1,5,1,4,1,4,0,5,0,5,-1,4,-1,4,-1,5,-1,5,-2,6,-2,6,-3,5,-3,5,-6,4,-6,4,-6,5,-6,5,-8,3,-8,3,-7,1,-7,1,-7,2,-7,2,-3,0,-3,0,-2,-2,-2,-2,-2,1,-2,1,0,-6,
	};
	private double scale=1.2;
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