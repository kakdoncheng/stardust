package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.VectorGraphics;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AntiMatterExplosion extends StardustEntity{
	public AntiMatterExplosion(StardustGame game, double x, double y, StardustEntity owner) {
		super(game);
		setXY(x, y);
		setBoundRadius(0);
		this.owner=owner;
		
		for(int i=0;i<360;i++){
			game.$currentState().addEntity(new Spark(game, x,y,16*game.$prng().$int(32, 46)));
		}
		Audio.addSoundEffect("explosion-emp", 1);
	}

	private int range=480;
	private double rr=0;
	private double rlast=0;
	private double fade=1;
	
	private StardustEntity owner;
	
	public void update(double dt) {
		rlast=rr-16;
		rr+=dt*range;
		if(rr>range){
			fade-=4*dt;
			if(fade<0){
				active=false;
			}
			return;
		}
		for(StardustEntity e:game.$currentState().$targetableEntities()){
			if(e==this || e==owner || !e.isCollidable()){
				continue;
			}
			if(rlast<distanceTo(e) && rr>distanceTo(e)){
				e.setKiller(this);
				e.deactivate();
				game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
			}
		}
		for(int i=0;i<2;i++){
			for(StardustEntity e:game.$dust(i)){
				double dist=Vector.distanceFromTo(game.$camera().$cx(x),game.$camera().$cy(y),
						game.$dustCamera(i).$cx(e.$x()),game.$dustCamera(i).$cy(e.$y()));
				if(rlast*(StardustGame.MAIN_CAMERA_ZOOM)<dist && rr*(StardustGame.MAIN_CAMERA_ZOOM)>dist){
					e.blip();
				}
			}
		}
	}

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setActualRadarColor(0.8*fade);
		VectorGraphics.renderVectorCircle(x, y, rr, 64, c);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}