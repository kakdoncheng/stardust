package stardust.entities.gyrus;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class GyrusProjectile extends GyrusEntity{

	public GyrusProjectile(StardustGame game, StardustEntity owner) {
		super(game, owner.$t(), owner.$speed());
		this.owner=owner;
		setBoundRadius(2);
		sp=240;
		spt=0;//game.$prng().$double(-0.05*Math.PI, 0.05*Math.PI);
		blip();
		Audio.addSoundEffect("fire-starfighter", 1);
	}
	
	private StardustEntity owner;
	private double sp;
	private double spt;
	public void update(double dt) {
		updateBlip(dt);
		
		t+=spt*dt;
		double ddx=sp*dt*$ndxscale();
		dx+=ddx;
		
		updateNormalizedXY();
		updateNormalizedBoundRadius();
		deactivateIfOutOfBounds();
		
		for(StardustEntity e:game.$currentState().$entities()){
			if(e instanceof GyrusProjectile||e instanceof GyrusShip||!e.isCollidable()){
				continue;
			}
			if(distanceTo(e)<this.$r()+e.$r()){
				active=false;
				e.setKiller(owner);
				e.deactivate();
				game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
				break;
			}
		}
	}
	
	//x1, y1, x2, y2 line render
	private double l[]={
			0,3,-1,2,-1,2,0,-11,0,-11,1,2,1,2,0,3,0,3,-2,-2,-2,-2,0,-4,0,-4,2,-2,2,-2,0,3,
	};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale*$ndxscale(), l[i+1]*c.$zoom()*scale*$ndxscale());
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale*$ndxscale(), l[i+3]*c.$zoom()*scale*$ndxscale());
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void onDeath() {
		
	}
}