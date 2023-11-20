package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AntiMatterProjectile extends Projectile{

	public AntiMatterProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 280, 320, owner);
		setBoundRadius(1);
		Audio.addSoundEffect("fire-blaster", 1);
	}

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(fade);
		GL11.glVertex2d(c.$cx($x()), c.$cy($y()));
		GL11.glVertex2d(c.$cx($x()+Vector.vectorToDx(t, 8)), c.$cy($y()+Vector.vectorToDy(t, 8)));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}

	protected void onImpactWith(StardustEntity e) {
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
		
	}

}
