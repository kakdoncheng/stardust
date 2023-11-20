package stardust.entities.terra;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.Vector;
import engine.gfx.Camera;

public class Frigate extends StardustEntity{
	
	public Frigate(StardustGame game, double x, double y, double t) {
		super(game);
		setXY(x, y);
		setBoundRadius(8);
		setDirection(t);
		setSpeedVector(t, 16);
	}
	
	private double rs=0;
	private double dtt=0;
	
	public void update(double dt){
		updateBlip(dt);
		for(StardustEntity e:game.$currentState().$entities()){
			if(e==this||!e.isCollidable()){
				continue;
			}
			if(distanceTo(e)<this.r+e.$r()){
				active=false;
				game.$currentState().addEntity(new Explosion(game, x, y, 12));
				break;
			}
		}
		updatePosition(dt);
		
		rs+=2*dt;
		if(rs>1){
			rs=1;
		}
		
		dtt-=dt;
		if(dtt<=0){
			double tdx=x+Vector.vectorToDx(t, -10);
			double tdy=y+Vector.vectorToDy(t, -10);
			game.$currentState().addEntity(new TracerDot(game,tdx,tdy,game.$prng().$double(1,2),this.alpha, 1));
			dtt+=1.0/30;
		}
			
	}

	private double l[]={
		-2,0,0,6,0,6,2,0,2,-8,2,2,2,2,3,0,3,0,3,-6,3,-6,2,-8,3,-6,6,-8,3,-1,6,-5,6,-9,6,-4,6,-4,7,-7,7,-7,6,-9,-2,2,-2,-8,-2,-8,-3,-6,-3,-6,-3,0,-3,0,-2,2,-3,-6,-6,-8,-3,-1,-6,-5,-6,-9,-6,-4,-6,-4,-7,-7,-7,-7,-6,-9,-2,-6,0,-7,0,-7,2,-6,0,4,-1,1,-1,1,0,0,0,0,1,1,1,1,0,4,
	};
	private double scale=1;

	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale*rs, l[i+1]*c.$zoom()*scale*rs);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale*rs, l[i+3]*c.$zoom()*scale*rs);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

	public void onDeath() {
		
	}
	
}