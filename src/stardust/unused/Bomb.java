package stardust.unused;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;

public class Bomb extends Projectile{

	public Bomb(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 0, 320, owner);
		setBoundRadius(4);
		setSpeedVector(0, 120);
		applyAccelerationVector(t, 45, 1);
		
	}
	
	private double tt;
	public void update(double dt){
		if(!active){
			return;
		}
		super.update(dt);
		if(t<Math.PI){
			t-=dt;
		}
		if(t>=Math.PI){
			t+=dt;
		}
		tt=t;
		if(t<0 || t>2*Math.PI){
			tt=0;
		}
	}

	//x1, y1, x2, y2 line render
	private double l[]={
			-1,1,1,1,1,1,2,0,2,0,2,-3,2,-3,1,-6,1,-6,2,-7,2,-7,2,-8,2,-8,-2,-8,-2,-8,-2,-7,-2,-7,-1,-6,-1,-6,-2,-3,-2,-3,-2,0,-2,0,-1,1,-2,0,2,0,2,-3,-2,-3,-1,-6,1,-6,1,-6,0,-8,0,-8,-1,-6,
		};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0.5,0,alpha);
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

	protected void onImpactWith(StardustEntity e) {
		if(e instanceof Projectile || e instanceof Fighter || e instanceof Bomber){
			active=true;
			return;
		}
		e.setTarget(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
	}

}
