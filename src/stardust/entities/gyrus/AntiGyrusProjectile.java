package stardust.entities.gyrus;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AntiGyrusProjectile extends GyrusEntity{
	
	private StardustEntity owner;
	public AntiGyrusProjectile(StardustGame game, double x, double y, StardustEntity owner) {
		super(game, Vector.directionFromTo(0,0,x,y), Vector.distanceFromTo(0,0,x,y));
		setBoundRadius(2);
		this.owner=owner;
		ddx=dx*2;
		tt=owner.$t()+(Math.PI*0.25);
		blip();
		Audio.addSoundEffect("fire-blasterlow", 1);
	}
	
	private double ddx;
	private double tt;
	public void update(double dt) {
		if(dx<=3 || !active){
			active=false;
			return;
		}
		updateBlip(dt);
		tt+=2*Math.PI*dt;
		dx-=ddx*dt*$ndxscale();
		for(StardustEntity e:game.$currentState().$entities()){
			if(e instanceof AntiGyrusProjectile||e==owner||!e.isCollidable()){
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
		updateNormalizedXY();
		updateNormalizedBoundRadius();
	}
	
	// x1, y1, x2, y2 line render
	private double l[]={
		0,-1,-1,0,-1,0,0,1,0,1,1,0,1,0,0,-1,-1,-1,-1,1,-1,1,1,1,1,1,1,-1,1,-1,-1,-1,
	};
	private double scale=1.5;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0,0,1);
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
