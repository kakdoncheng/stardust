package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class PowerBomb extends Projectile{

	public PowerBomb(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 160, 160, owner);
		setBoundRadius(2.5);
		game.$currentState().addEntity(new Explosion(game, x, y, 16));
		Audio.addSoundEffect("fire-energy", 0.75f);
		Audio.addSoundEffect("fire-bomb", 1);
	}
	
	private double dtt=0;
	public void update(double dt) {
		// slow to dead stop,
		// drop range on stop to trigger death
		speed-=80*dt;
		setSpeedVector(t, speed);
		if(speed>40) {
			range=9001;
		} else {
			range=-1;
		}
		
		super.update(dt);
		blip();
		
		dtt-=dt;
		if(dtt<=0){
			double tt=game.$prng().$double(0, 2*Math.PI);
			double di=game.$prng().$double(0, r*0.25);
			double dx=Vector.vectorToDx(tt, di);
			double dy=Vector.vectorToDy(tt, di);
			game.$currentState().addEntity(new PowerBombTracerDot(game,x+dx,y+dy,game.$prng().$double(r*0.5, r*2)));
			dtt+=1.0/60;
		}
		//t+=dt;
	}

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
		
		GL11.glColor4d(1, 1, 1, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		int seg=8;
		double ci=2*Math.PI;
		double cis=ci/seg;
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,r), 
					dxyy1=Vector.vectorToDy(i,r), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r);
			GL11.glVertex2d(dxyx1*c.$zoom(), dxyy1*c.$zoom());
			GL11.glVertex2d(dxyx2*c.$zoom(), dxyy2*c.$zoom());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		game.$currentState().addEntity(new PowerBombExplosion(game, x, y, owner));
	}

	protected void onImpactWith(StardustEntity e) {
		deactivate();
	}

}
