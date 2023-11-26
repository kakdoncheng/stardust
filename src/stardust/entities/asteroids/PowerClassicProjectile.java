package stardust.entities.asteroids;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.entities.AntiMatterBomb;
import stardust.entities.Power;
import stardust.entities.StardustEntity;
import stardust.states.EndlessState;

public class PowerClassicProjectile extends Power{

	public PowerClassicProjectile(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
		ammo=120;
		wild=game.$currentState() instanceof EndlessState;
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.05){
			ClassicProjectile cp=new ClassicProjectile(game, owner.$t(), owner);
			cp.applyAccelerationVector(owner.$speedt(), owner.$speed(), 1);
			game.$currentState().addEntity(cp);
			cp=new ClassicProjectile(game, owner.$t()+game.$prng().$double(-0.125, 0.125), owner);
			cp.applyAccelerationVector(owner.$speedt(), owner.$speed(), 1);
			game.$currentState().addEntity(cp);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
	public void useSecondary(StardustEntity owner){
		ammo=0;
		game.$currentState().addEntity(new AntiMatterBomb(game, owner.$t(), owner));
	}
	
	private boolean wild;
	
	// override methods
	public void update(double dt) {
		//blip();
		updateBlip(dt);
		rt+=Math.PI*dt;
		
		if(target!=null && distanceTo(target)<120){
			rotateTowards(target, 3*Math.PI, dt);
			setSpeedVector(t, speed+(120-distanceTo(target)));
		}else{
			setSpeedVector(t, speed);
		}
		
		updatePosition(dt);
		if(wild) {
			deactivateIfOutOfBounds();
		} else {
			wraparoundIfOutOfScreenBounds();
		}
		//
	}
	
	private double li[]={
		0,-2,2,0,2,0,1,0,1,0,1,2,1,2,-1,2,-1,2,-1,0,-1,0,-2,0,-2,0,0,-2,
	};
	private double scalei=2;
	public void render(Camera c) {
		// render hud indicator
		double hdx=160;
		if(wild && target!=null && Vector.distanceFromTo(c.$dx(), c.$dy(), x, y)>hdx){//distanceTo(target)>hdx){
			//double tt=target.directionTo(this);
			//double tx=target.$x()+Vector.vectorToDx(tt, hdx);
			//double ty=target.$y()+Vector.vectorToDy(tt, hdx);
			double tt=Vector.directionFromTo(c.$dx(), c.$dy(), x, y);
			double tx=c.$zoom()*Vector.vectorToDx(tt, hdx);
			double ty=c.$zoom()*Vector.vectorToDy(tt, hdx);
			GL11.glColor4d(1, 0.5, 0, 1);
			GL11.glColor4d(1, 0.5, 0, alpha);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			//GL11.glTranslated(c.$cx(tx), c.$cy(ty), 0);
			GL11.glTranslated(tx, ty, 0);
			GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
			GL11.glBegin(GL11.GL_LINES);
			int seg=3;
			int ir=3;
			double ci=2*Math.PI;
			double cis=ci/seg;
			for(double i=0;i<ci;i+=cis){
				double dxyx1=Vector.vectorToDx(i,ir), 
						dxyy1=Vector.vectorToDy(i,ir), 
						dxyx2=Vector.vectorToDx(i+(ci/seg),ir),
						dxyy2=Vector.vectorToDy(i+(ci/seg),ir);
				GL11.glVertex2d(dxyx1*c.$zoom(), dxyy1*c.$zoom());
				GL11.glVertex2d(dxyx2*c.$zoom(), dxyy2*c.$zoom());
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			//return;
		}
		
		GL11.glColor4d(1, 0.5, 0, 1);
		//setRadarColor(1);
		
		// render body
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(rt), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		//for(int i=0; i<l.length; i+=4){
		//	GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
		//	GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		//}
		int seg=5;
		int ir=5;
		double ci=2*Math.PI;
		double cis=ci/seg;
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,ir), 
					dxyy1=Vector.vectorToDy(i,ir), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),ir),
					dxyy2=Vector.vectorToDy(i+(ci/seg),ir);
			GL11.glVertex2d(dxyx1*c.$zoom(), dxyy1*c.$zoom());
			GL11.glVertex2d(dxyx2*c.$zoom(), dxyy2*c.$zoom());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render icon
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
		GL11.glBegin(GL11.GL_LINES);
		for(int i=0; i<li.length; i+=4){
			GL11.glVertex2d(li[i]*c.$zoom()*scalei, li[i+1]*c.$zoom()*scalei);
			GL11.glVertex2d(li[i+2]*c.$zoom()*scalei, li[i+3]*c.$zoom()*scalei);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
