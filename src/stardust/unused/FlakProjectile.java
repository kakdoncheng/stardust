package stardust.unused;

import engine.gfx.Camera;
import engine.sfx.Audio;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.terra.Terra;

public class FlakProjectile extends Projectile{

	public FlakProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 320, 640, owner);
		setBoundRadius(2);
		Audio.addSoundEffect("fire-slug", 1);
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		0,1,-1,0,-1,0,-1,-1,-1,-1,0,-6,0,-6,1,-1,1,-1,1,0,1,0,0,1,
	};
	private double scale=1;
	public void render(Camera c) {
		// render
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
		
	}

	protected void onImpactWith(StardustEntity e) {
		if(e instanceof Terra){
			active=true;
			//game.$currentState().addEntity(new Explosion(game, x, y, 4));
			return;
		}
		e.setTarget(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
	}
}
