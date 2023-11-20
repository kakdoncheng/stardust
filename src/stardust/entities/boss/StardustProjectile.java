package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class StardustProjectile extends Projectile{

	public StardustProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 160, 640, owner);
		setBoundRadius(2);
		Audio.addSoundEffect("fire-stardust", 1);
	}

	private double tt=0;
	public void update(double dt) {
		super.update(dt);
		tt+=3*dt*Math.PI;
	}
	
	public void render(Camera c){
		double s=1.5*c.$zoom();
		double ds=2;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(tt), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0.5,0,alpha*fade);
		setRadarColor(fade);
		GL11.glVertex2d(-s*ds/2, -s*ds/2);
		GL11.glVertex2d(-s/2, s/2);
		GL11.glVertex2d(-s/2, s/2);
		GL11.glVertex2d(s*ds/2, s*ds/2);
		GL11.glVertex2d(s*ds/2, s*ds/2);
		GL11.glVertex2d(s/2, -s/2);
		GL11.glVertex2d(s/2, -s/2);
		GL11.glVertex2d(-s*ds/2, -s*ds/2);
		
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}

	protected void onImpactWith(StardustEntity e) {
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
	}

}