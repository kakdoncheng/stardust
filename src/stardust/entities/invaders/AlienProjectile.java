package stardust.entities.invaders;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AlienProjectile extends Projectile{

	public AlienProjectile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 160, 320, owner);
		setBoundRadius(4);
		ox=owner.$x();
		oy=owner.$y();
		ai=game.$prng().$int(0, 2);
		Audio.addSoundEffect("fire-bogey", 1);
	}
	
	private double ox;
	private double oy;
	private int ai;
	
	public void update(double dt) {
		super.update(dt);
		this.alpha=1;
	}

	private double[][] l={
		//{2,0,2,1,2,1,1,1,1,1,1,2,1,2,0,2,0,2,0,3,0,3,-1,3,-1,3,-1,4,-1,4,0,4,0,4,0,5,0,5,1,5,1,5,1,6,1,6,2,6,2,6,2,7,2,7,1,7,1,7,1,8,1,8,0,8,0,8,0,9,0,9,-1,9,-1,9,-1,10,-1,10,0,10,0,10,0,11,0,11,1,11,1,11,1,12,1,12,2,12,2,12,2,13,2,13,1,13,1,13,1,12,1,12,0,12,0,12,0,11,0,11,-1,11,-1,11,-1,10,-1,10,-2,10,-2,10,-2,9,-2,9,-1,9,-1,9,-1,8,-1,8,0,8,0,8,0,7,0,7,1,7,1,7,1,6,1,6,0,6,0,6,0,5,0,5,-1,5,-1,5,-1,4,-1,4,-2,4,-2,4,-2,3,-2,3,-1,3,-1,3,-1,2,-1,2,0,2,0,2,0,1,0,1,1,1,1,1,1,0,1,0,2,0,},
		//{-1,-1,-2,0,-2,0,-1,1,-1,1,-1,6,-1,6,1,6,1,6,1,1,1,1,2,0,2,0,1,-1,1,-1,1,-6,1,-6,-1,-6,-1,-6,-1,-1,}
		{2,0,2,1,2,1,1,1,1,1,1,2,1,2,0,2,0,2,0,3,0,3,-1,3,-1,3,-1,4,-1,4,0,4,0,4,0,5,0,5,1,5,1,5,1,6,1,6,2,6,2,6,2,7,2,7,1,7,1,7,1,8,1,8,0,8,0,8,0,9,0,9,-1,9,-1,9,-1,10,-1,10,-2,10,-2,10,-2,9,-2,9,-1,9,-1,9,-1,8,-1,8,0,8,0,8,0,7,0,7,1,7,1,7,1,6,1,6,0,6,0,6,0,5,0,5,-1,5,-1,5,-1,4,-1,4,-2,4,-2,4,-2,3,-2,3,-1,3,-1,3,-1,2,-1,2,0,2,0,2,0,1,0,1,1,1,1,1,1,0,1,0,2,0,},
		{3,0,1,-2,1,-2,1,-5,1,-5,-1,-5,-1,-5,-1,-2,-1,-2,-3,0,-3,0,-1,2,-1,2,-1,5,-1,5,1,5,1,5,1,2,1,2,3,0,},

	};
	private double scale=0.625;
	public void render(Camera c) {
		// calculate render position + flip
		double b=9;
		double ar=(320-range);
		double adjr=ar-(ar%(b*scale));
		double rx=ox+Vector.vectorToDx(t, adjr);
		double ry=oy+Vector.vectorToDy(t, adjr);
		
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(rx), c.$cy(ry), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		if(ai<1){
			if(ar%(b*2*scale)>=b*scale){
				GL11.glRotatef(180, 0, 1, 0);
			}
		}
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l[ai].length; i+=4){
			GL11.glVertex2d(l[ai][i]*c.$zoom()*scale, l[ai][i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[ai][i+2]*c.$zoom()*scale, l[ai][i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}
	
	public boolean isCollidable(){
		return false;
	}

	protected void onImpactWith(StardustEntity e) {
		active=true;
		if(owner instanceof Alien && e instanceof Alien){
			active=true;
			return;
		}
		e.setKiller(owner);
		e.setTarget(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
	}

}