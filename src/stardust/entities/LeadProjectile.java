package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.VectorGraphics;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class LeadProjectile extends Projectile{

	public LeadProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 320, 280, owner);
		setBoundRadius(1);
		//setBoundRadius(game.$prng().$int(-1, 3));
		//if(this.$r()<1){
		//	setBoundRadius(1);
		//}
		this.applyAccelerationVector(game.$prng().$double(0, Math.PI*2), game.$prng().$int(0, 60), 1);
		Audio.addSoundEffect("fire-blaster", 1);
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
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
	}
}
