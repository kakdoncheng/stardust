package stardust.entities.demonstar;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class MartianProjectile extends Projectile{

	public MartianProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 160, 640, owner);
		setBoundRadius(2);
		Audio.addSoundEffect("fire-martian", 1);
	}
	
	private double tt=0;
	public void update(double dt){
		super.update(dt);
		tt+=4*dt;
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		0,-4,-3,-3,-3,-3,-4,0,-4,0,-3,3,-3,3,0,4,0,4,3,3,3,3,4,0,4,0,3,-3,3,-3,0,-4,-1,-3,-3,-1,-3,-1,-4,-4,-4,-4,-1,-3,1,-3,3,-1,3,-1,4,-4,4,-4,1,-3,-3,1,-1,3,-1,3,-4,4,-4,4,-3,1,3,1,1,3,1,3,4,4,4,4,3,1,3,-1,4,0,4,0,3,1,-1,3,0,4,0,4,1,3,-3,1,-4,0,-4,0,-3,-1,-1,-3,0,-4,0,-4,1,-3,1,-3,-1,-3,-3,-1,-3,1,-1,3,1,3,3,-1,3,1,
	};
	private double scale=0.325;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
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
		
	}

	protected void onImpactWith(StardustEntity e) {
		if(e instanceof MartianShip){
			active=true;
			return;
		}
		e.setTarget(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
	}
}