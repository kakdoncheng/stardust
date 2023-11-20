package stardust.unused;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;

public class EnergyParticle extends StardustEntity{

	public EnergyParticle(StardustGame game, double x, double y, double t) {
		super(game);
		this.setBoundRadius(0);
		this.setXY(x, y);
		this.setDirection(t);
	}

	public void update(double dt) {
		scale-=4*dt;
		if(scale<0){
			deactivate();
		}
	}

	private double scale=2;
	private double l[]={
			0,-1,1,0,1,0,0,1,0,1,-1,0,-1,0,0,-1,
	};
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);	
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		return false;
	}
	
	public void onDeath() {
		if(killer!=null){
			game.$currentState().addEntity(new ElectromagneticPulse(game, x, y));
		}
	}

}