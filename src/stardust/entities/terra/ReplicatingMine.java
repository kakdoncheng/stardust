package stardust.entities.terra;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.silo.AnnihilatingExplosion;

public class ReplicatingMine extends Projectile{
	
	public int points(){
		return 20;
	}
	
	public ReplicatingMine(StardustGame game, double x, double y, double t) {
		super(game, x, y, t, 12, 12000, null);
		setBoundRadius(8);
	}
	
	private double tt=game.$prng().$double(0, Math.PI*2);
	private double rs=0;
	
	public void update(double dt){
		super.update(dt);
		tt+=dt;
		tt=Vector.constrainTheta(tt);
		rs+=2*dt;
		if(rs>1){
			rs=1;
		}
		deactivateIfOutOfBounds();
	}

	private double l[]={
		0,-4,-3,-3,-3,-3,-4,0,-4,0,-3,3,-3,3,0,4,0,4,3,3,3,3,4,0,4,0,3,-3,3,-3,0,-4,-3,-2,-2,-2,-2,-2,-2,-3,-2,-3,-5,-5,-5,-5,-3,-2,-3,2,-2,2,-2,2,-2,3,-2,3,-5,5,-5,5,-3,2,2,-3,2,-2,2,-2,3,-2,3,-2,5,-5,5,-5,2,-3,3,2,2,2,2,2,2,3,2,3,5,5,5,5,3,2,3,-1,2,0,2,0,3,1,3,1,5,0,5,0,3,-1,0,-2,-1,-3,-1,-3,0,-5,0,-5,1,-3,1,-3,0,-2,-2,0,-3,-1,-3,-1,-5,0,-5,0,-3,1,-3,1,-2,0,0,2,-1,3,-1,3,0,5,0,5,1,3,1,3,0,2,0,-1,-1,0,-1,0,0,1,0,1,1,0,1,0,0,-1,
	};
	private double scale=1.75;

	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
		
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

	protected void onImpactWith(StardustEntity e) {
		// will not detonate if it hits a projectile
		if(e instanceof Projectile){
			active=true;
			return;
		}
		game.$currentState().addEntity(new AnnihilatingExplosion(game, x, y, 16, this));
		game.$currentState().addEntity(new ReplicatingMineSwarm(game, x, y));
	}
	
}
