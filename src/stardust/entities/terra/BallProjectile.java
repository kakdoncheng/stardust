package stardust.entities.terra;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.gfx.VectorGraphics;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class BallProjectile extends Projectile{

	public BallProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 200, 280, owner);
		setBoundRadius(1.5);
		Audio.addSoundEffect("fire-slug", 1);
	}

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(fade);
		VectorGraphics.renderVectorCircle(x, y, r, 8, c);
		GL11.glEnd();
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
