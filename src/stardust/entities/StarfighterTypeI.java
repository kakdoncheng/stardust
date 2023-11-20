package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.boss.InfernoBolt;
import engine.Vector;
import engine.gfx.Camera;

public class StarfighterTypeI extends Starcraft{
	
	public int points() {
		return 80;
	}
	
	public StarfighterTypeI(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
	}
	
	private double aF=120;
	private double cooldown=1;
	
	private int ammo=2;
	private double reload=0;
	
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
		
		if(ammo<1){
			reload+=dt;
			if(reload>=3){
				ammo=2;
				reload=0;
			}
		}else if(distanceTo(target)<280){
    		cooldown+=dt;
    		if(cooldown>0.25){
    			game.$currentState().addEntity(new Explosion(game,x,y,8));
    			StardustEntity e=new InfernoBolt(game, t, this);
    			game.$currentState().addEntity(e);
    			cooldown=0;
    			ammo--;
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
		-2,-4,-2,-5,-2,-5,-1,-6,-1,-6,1,-6,1,-6,2,-5,2,-5,2,-4,0,-5,-2,-4,-2,-4,-3,-4,-3,-4,-4,0,-4,0,-5,-4,-5,-4,-10,-6,-10,-6,-4,2,-5,-4,-5,-5,-5,-5,-4,-6,-4,-6,-3,-5,-3,-5,-3,-4,0,-5,2,-4,2,-4,3,-4,3,-4,3,-5,3,-5,4,-6,4,-6,5,-5,5,-5,5,-4,5,-4,4,0,4,0,3,-4,0,-5,-4,2,-4,2,0,4,0,4,4,2,4,2,0,-5,5,-4,10,-6,10,-6,4,2,0,4,-2,2,-2,2,-2,1,-2,1,0,0,0,0,2,1,2,1,2,2,2,2,0,4,-4,2,-2,4,-2,4,-2,3,2,3,2,4,2,4,4,2,
	};
	private double scale=1.2;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
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
