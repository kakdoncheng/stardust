package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class InfernoBolt extends Projectile{

	public InfernoBolt(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 120, 640, owner);
		setBoundRadius(3);
		Audio.addSoundEffect("fire-inferno", 1);
	}

	private double tt=0;
	private double dtt=0;
	public void update(double dt) {
		super.update(dt);
		tt+=3*dt*Math.PI;
		dtt-=dt;
		if(dtt<=0){
			double tt=game.$prng().$double(0, 2*Math.PI);
			double di=game.$prng().$double(0, r*0.25);
			double dx=Vector.vectorToDx(tt, di);
			double dy=Vector.vectorToDy(tt, di);
			game.$currentState().addEntity(new TracerDot(game,x+dx,y+dy,game.$prng().$double(1, 3),this.alpha,2,game.$prng().$int(4, 8)));
			dtt+=1.0/30;
		}
	}
	
	private double l[]={
		0,-4,-3,-3,-3,-3,-4,0,-4,0,-3,3,-3,3,0,4,0,4,3,3,3,3,4,0,4,0,3,-3,3,-3,0,-4,-1,-3,1,-3,-1,-3,0,-6,0,-6,1,-3,-3,-1,-3,1,-3,1,-6,0,-6,0,-3,-1,-1,3,1,3,1,3,0,6,0,6,-1,3,3,-1,3,1,3,1,6,0,6,0,3,-1,
	};
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
		
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
		if(range>0){
			active=true;
		}
	}

	public boolean isCollidable(){
		return false;
	}
	
	protected void onImpactWith(StardustEntity e) {
		active=true;
		if(e instanceof InfernoBolt){
			return;
		}
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
	}
}
