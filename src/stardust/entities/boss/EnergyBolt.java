package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class EnergyBolt extends Projectile{

	public EnergyBolt(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 240, 1280, owner);
		setBoundRadius(3);
		Audio.addSoundEffect("fire-energy", 1);
	}
	
	private double dtr=0.5;
	private boolean rebound=false;
	
	public void update(double dt){
		super.update(dt);
		if(target!=null && target.isActive()){
			dtr-=dt;
			if(dtr<=0 && !rebound){
				double nt=directionTo(target);
				setDirection(nt);
				setSpeedVector(nt, 240);
				rebound=true;
				//Audio.playSoundEffect("fire-rebound", 1);
			}
		}
	}
	
	private double l[]={
		-1,-1,-2,0,-2,0,-1,1,1,-1,2,0,2,0,1,1,-1,-1,0,-8,0,-8,1,-1,-1,1,0,8,0,8,1,1,
	};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
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
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
	}
}
