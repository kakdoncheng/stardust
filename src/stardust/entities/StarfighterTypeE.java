package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.boss.EnergyBolt;
import engine.Vector;
import engine.gfx.Camera;

public class StarfighterTypeE extends Starcraft{
	
	public int points() {
		return 80;
	}
	
	public StarfighterTypeE(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
	}
	
	private double aF=160;
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
		if (distanceTo(target)>192) {
			applyAccelerationVector(t, aF, dt);
			isMoving=true;
    	}
		
		if(distanceTo(target)<280){
    		cooldown+=dt;
    		if(cooldown>1){
    			game.$currentState().addEntity(new Explosion(game,x,y,8));
    			StardustEntity e=new EnergyBolt(game, t, this);
    			e.setTarget(target);
    			game.$currentState().addEntity(e);
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
		-10,-10,-7,-2,-7,-2,-6,-3,-6,-3,-2,0,-2,0,-4,-1,-4,-1,-2,5,-2,5,-2,2,-2,2,0,8,0,8,2,2,2,2,2,5,2,5,4,-1,4,-1,2,0,2,0,6,-3,6,-3,7,-2,7,-2,10,-10,10,-10,2,-8,2,-8,2,-10,2,-10,1,-11,1,-11,-1,-11,-1,-11,-2,-10,-2,-10,-2,-8,-2,-8,-10,-10,-2,-8,0,-3,0,-3,2,-8,0,8,-1,3,-1,3,0,2,0,2,1,3,1,3,0,8,-2,-8,-3,-10,-3,-10,-6,-9,2,-8,3,-10,3,-10,6,-9,6,-9,2,0,2,0,2,-8,-2,-8,-2,0,-2,0,-6,-9,
	};
	private double scale=0.8;
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

