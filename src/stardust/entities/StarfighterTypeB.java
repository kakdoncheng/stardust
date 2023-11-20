package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.silo.AnnihilatingMissile;

public class StarfighterTypeB extends Starcraft{
	
	public int points() {
		return 15;
	}
	
	public StarfighterTypeB(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
	}
	
	private double aF=200;
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
		if (distanceTo(target)>200) {
			applyAccelerationVector(t, aF, dt);
			isMoving=true;
    	}
		cooldown+=dt;
		if(cooldown>2.0){
			// recoil
			applyAccelerationVector(t+Math.PI, 8, 1);
			
			AnnihilatingMissile e=new AnnihilatingMissile(game, target.$x(), target.$y(), t+game.$prng().$double(-Math.PI/4, Math.PI/4), this);
			e.follow((StardustEntity) target);
			game.$currentState().addEntity(e);
			cooldown=0;
		}
    	
		
		// friction brakes
		double cs=$speed();
		if(cs>(isMoving?0:8)){
			double ft=Vector.constrainTheta(Vector.dxyToDirection(dx, dy)+Math.PI);
			double fF=isMoving?cs*cs*0.005:aF/2;
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
		-2,0,-1,-1,-1,-1,1,-1,1,-1,2,0,2,0,2,-5,2,-5,5,-3,5,-3,5,1,5,1,11,2,11,2,11,1,11,1,12,2,12,2,12,4,12,4,5,5,5,5,5,6,5,6,2,7,2,7,2,6,2,6,0,7,0,7,-2,6,-2,6,-2,7,-2,7,-5,6,-5,6,-5,5,-5,5,-12,4,-12,4,-12,2,-12,2,-11,1,-11,1,-11,2,-11,2,-5,1,-5,1,-5,-3,-5,-3,-2,-5,-2,-5,-2,0,
		//0,4,-2,-1,-2,-1,-2,2,-2,2,-4,-1,-4,-1,-10,-4,-10,-4,-10,-5,-10,-5,-3,-6,-3,-6,-2,-9,-2,-9,-1,-6,-1,-6,0,-7,0,-7,1,-6,1,-6,2,-9,2,-9,3,-6,3,-6,10,-5,10,-5,10,-4,10,-4,4,-1,4,-1,2,2,2,2,2,-1,2,-1,0,4,0,3,-1,0,-1,0,0,-2,0,-2,1,0,1,0,0,3,
	};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
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
