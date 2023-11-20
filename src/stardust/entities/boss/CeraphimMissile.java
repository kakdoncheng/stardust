package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.entities.Entity;
import engine.gfx.Camera;

public class CeraphimMissile extends Projectile{

	public CeraphimMissile(StardustGame game, double t, StardustEntity owner, Entity target) {
		super(game, owner.$x(), owner.$y(), t, 240, 640, owner);
		setBoundRadius(2);
		setTarget(target);
	}

	private double dtt=0;
	private double rt=game.$prng().$double(-1, 3);
	private double rtb=game.$prng().$double(0, 1);
	public void update(double dt) {
		super.update(dt);
		blip();
		if(rt>0){
			rt-=dt;
			if(rtb>0.5){
				t+=Math.PI*1.5*dt;
			}else{
				t-=Math.PI*1.5*dt;
			}
		}else{
			rotateTowards(target, Math.PI*2, dt);
		}
		
		setSpeedVector(t, $speed());
		dtt-=dt;
		if(dtt<=0){
			game.$currentState().addEntity(new TracerDot(game,x,y,1,fade,1));
			dtt+=1.0/60;
		}
		t+=dt;
	}
	
	// x1, y1, x2, y2 line render
	private double l[]={
		-2,-2,0,8,0,8,2,-2,2,-2,1,0,1,0,-1,0,-1,0,-2,-2,
	};
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(fade);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

	public void onDeath() {
		//game.$currentState().addEntity(new Explosion(game, x, y, 8));
	}

	protected void onImpactWith(StardustEntity e) {
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
	}

}