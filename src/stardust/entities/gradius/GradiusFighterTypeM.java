package stardust.entities.gradius;

import engine.Vector;
import engine.entities.Entity;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;

public class GradiusFighterTypeM extends GradiusShip{
	public int points(){
		return 30;
	}
	
	public GradiusFighterTypeM(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(Math.PI*0.5);
		setSpeedVector(t,60);
	}
	
	private double dtt=0;
	private double fuse=3;
	private boolean primed=true;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		fuse-=dt;
		if(fuse<=0){
			if(primed){
				game.$currentState().addEntity(new Explosion(game, x, y, 12));
				for(int i=0;i<5;i++){
					game.$currentState().addEntity(new GradiusProjectile(game, Math.PI*1.25+(Math.PI*0.125*i), this));
				}
				setSpeedVector(t,240);
				primed=false;
			}
			if(x<game.$leftScreenEdge()){
				active=false;
				return;
			}
			blip();
			dtt-=dt;
			if(dtt<=0){
				double tt=game.$prng().$double(0, 2*Math.PI);
				double di=game.$prng().$double(0, r*0.25);
				double dx=Vector.vectorToDx(tt, di);
				double dy=Vector.vectorToDy(tt, di);
				game.$currentState().addEntity(new TracerDot(game,x+dx,y+dy,game.$prng().$double(2, 4),this.alpha,4,game.$prng().$int(4, 8)));
				dtt+=1.0/30;
			}
			// destroy all objects it collides with
			for(StardustEntity e:game.$currentState().$entities()){
				if(e instanceof GradiusShip || !e.isCollidable()){
					continue;
				}
				if(distanceTo(e)<this.r+e.$r()){
					e.setKiller(this);
					e.deactivate();
					game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
				}
			}
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}
	
	public void deactivate(){
		active=fuse<=0;
	}
	public void setKiller(Entity e){
		if(fuse<=0){
			return;
		}
		super.setKiller(e);
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-1,-2,-2,-1,-2,-1,-2,0,-2,0,2,0,2,0,2,-1,2,-1,1,-2,-2,-2,2,-2,2,-2,2,-3,2,-3,-2,-3,-2,-3,-2,-2,-1,-3,-2,-4,-2,-4,2,-4,2,-4,1,-3,-2,-1,-3,-2,-3,-2,-3,-3,-3,-3,-6,-1,-6,-1,-6,0,-6,0,-2,0,2,-1,3,-2,3,-2,3,-3,3,-3,6,-1,6,-1,6,0,6,0,2,0,-5,0,-5,2,5,0,5,2,-2,0,-3,2,2,0,3,2,-6,2,6,2,6,2,6,6,6,6,3,9,3,9,3,10,3,10,2,10,2,10,2,6,2,6,3,9,-6,2,-6,6,-6,6,-3,9,-3,9,-3,10,-3,10,-2,10,-2,10,-2,6,-2,6,-3,9,-2,7,0,6,0,6,2,7,-2,8,0,9,0,9,2,8,-4,2,-4,5,-4,5,-2,6,4,2,4,5,4,5,2,6,-4,5,0,3,0,3,4,5,-6,-1,-8,-6,-8,-6,-3,-3,3,-3,8,-6,8,-6,6,-1,
	};
	private double scale=0.825;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0.5,0,alpha);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		if(fuse<=0){
			game.$currentState().addEntity(new Explosion(game, x, y, 32));
		}
	}
}
