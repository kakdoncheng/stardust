package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.VectorGraphics;

public class PortalScreenPlayer extends StardustEntity{

	public PortalScreenPlayer(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(0);
		this.setXY(x, y);
		rc=new RadarScan(game,0,0);
		rc.lockOnEntity(this);
	}
	
	private double aF=500;
	private RadarScan rc;
	
	public void update(double dt) {
		
		// movement input
		boolean hasInput=false;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			applyAccelerationVector(Math.PI, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			applyAccelerationVector(Math.PI*0.5, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			applyAccelerationVector(0, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			applyAccelerationVector(Math.PI*1.5, aF, dt);
			hasInput=true;
		}
		
		// friction brakes
		// F = v^2 * 0.5pDA
		double cs=$speed();
		if(cs>(hasInput?0:8)){
			double ft=Vector.constrainTheta(Vector.dxyToDirection(dx, dy)+Math.PI);
			double fF=hasInput?cs*cs*0.005:aF;
			applyAccelerationVector(ft, fF, dt);
		}else{
			dx=0;
			dy=0;
		}
		
		updatePosition(dt);
		
		// dir
		t=Vector.directionFromTo(x, y, MouseHandler.$mx(), MouseHandler.$my());
		rc.update(dt);
	}

	// 4xy render code
	private double scale=1.75;
	private double l[]={
		-1,0,0,2,0,2,1,0,1,0,1,4,1,4,2,1,2,1,5,-1,5,-1,5,-2,5,-2,2,-3,2,-3,1,-2,1,-2,0,-3,0,-3,-1,-2,-1,-2,-2,-3,-2,-3,-5,-2,-5,-2,-5,-1,-5,-1,-2,1,-2,1,-1,4,-1,4,-1,0,
	};

	public void render(Camera c) {
		//render ship
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);	
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,game.$currentSecond()%0.5>=0.25?1:0);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render cursor
		VectorGraphics.renderDotCursor();
		rc.render(c);
	}

	public boolean isCollidable(){
		return false;
	}
	
	public void onDeath() {
	}
	

}
