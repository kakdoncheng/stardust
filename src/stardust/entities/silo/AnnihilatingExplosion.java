package stardust.entities.silo;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AnnihilatingExplosion extends StardustEntity{
	
	private double dr;
	private boolean expand;
	private StardustEntity owner;

	public AnnihilatingExplosion(StardustGame game, double x, double y, int r, StardustEntity owner) {
		this(game, x, y, r, owner, false);
	}
	
	public AnnihilatingExplosion(StardustGame game, double x, double y, int r, StardustEntity owner, boolean mute) {
		super(game);
		setXY(x,y);
		setBoundRadius(1);
		blip();
		dr=r*2;
		expand=true;
		this.owner=owner;
		if(!mute){
			Audio.addSoundEffect("explosion-nuke", 1);
		}
	}

	public void update(double dt) {
		if(!active){
			return;
		}
		//updateBlip(dt);
		if(expand){
			r+=dr*2*dt;
			if(r>=dr){
				r=dr;
				expand=false;
			}
		}else{
			r-=dr*2*dt;
			if(r<=0){
				deactivate();
			}
		}
		
		
		
		// destroy all collidable entities in blast radius
		for(StardustEntity e:game.$currentState().$targetableEntities()){
			if(!e.isCollidable()){
				continue;
			}
			if(distanceTo(e)<this.r+e.$r()){
				e.setKiller(owner);
				e.deactivate();
				game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), 8));
			}
		}
	}

	public void render(Camera c) {
		int seg=16;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r), 
					dxyy1=Vector.vectorToDy(i,r), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r);
			GL11.glVertex2d(c.$cx(x+dxyx1), c.$cy(y+dxyy1));
			GL11.glVertex2d(c.$cx(x+dxyx2), c.$cy(y+dxyy2));
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}