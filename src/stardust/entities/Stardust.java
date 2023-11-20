package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.gfx.Camera;

public class Stardust extends StardustEntity{

	protected double tt;
	public Stardust(StardustGame game, double x, double y, int size) {
		super(game);
		this.setXY(x, y);
		this.setDirection(0);
		this.setSpeedVector(0, 24);
		if(size<0){
			this.setBoundRadius(1);
		}else{
			this.setBoundRadius(size);
		}
		tt=(game.$prng().$double(Math.PI, 3*Math.PI));
	}
	
	public void update(double dt) {
		t+=tt*dt;
		updateBlip(dt);
		updatePosition(dt);
	}
	
	public void render(Camera c) {
		double rr=r*c.$zoom();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_QUADS);
		setRadarColor(1);
		GL11.glVertex2d(-rr/2, -rr/2);
		GL11.glVertex2d(-rr/2, rr/2);
		GL11.glVertex2d(rr/2, rr/2);
		GL11.glVertex2d(rr/2, -rr/2);
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public void blip(){
		super.blip();
		alpha=0.75;
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
