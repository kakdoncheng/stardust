package stardust.entities.silo;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.MissileProjectile;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AnnihilatingMissile extends MissileProjectile{

	public AnnihilatingMissile(StardustGame game, double tx, double ty, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), tx, ty, t, 300, 1200, owner);
		Audio.addSoundEffect("fire-nuke", 1);
	}
	
	private double dtt=0;
	private boolean showtxy=false;
	private StardustEntity lock;
	public void follow(StardustEntity e){
		lock=e;
	}
	public void showTXY(){
		showtxy=true;
	}
	public void update(double dt){
		if(lock!=null){
			this.lockOnTarget(lock);
		}
		super.update(dt);
		dtt-=dt;
		if(dtt<=0){
			game.$currentState().addEntity(new TracerDot(game,x,y,1));
			dtt+=1.0/60;
		}
		blip();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		//-2,0,-2,4,-2,4,-1,7,-1,7,0,8,0,8,1,7,1,7,2,4,2,4,2,0,-2,0,-1,-1,-1,-1,1,-1,1,-1,2,0,-1,-1,-3,-2,-3,-2,-2,0,1,-1,3,-2,3,-2,2,0,2,0,-2,0,-2,4,2,4,1,7,-1,7,
		//-2,0,2,0,-2,0,-1,1,-1,1,1,1,1,1,2,0,-3,1,3,1,3,1,3,2,3,2,-3,2,-3,2,-3,1,-3,2,-3,20,-3,20,-2,24,-2,24,0,25,0,25,2,24,2,24,3,20,3,20,3,2,-3,20,3,20,3,19,-3,19,-2,24,2,24,-3,19,-5,3,-5,3,-3,2,3,2,5,3,5,3,3,19,0,19,-1,3,-1,3,0,2,0,2,1,3,1,3,0,19,-1,3,1,3,
		-1,0,-2,-1,-2,-1,2,-1,2,-1,1,0,-4,0,4,0,-4,0,-4,20,-4,20,4,20,4,20,4,0,-4,20,-3,28,-3,28,3,28,4,20,3,28,-3,28,-3,46,-3,46,-2,47,-2,47,0,48,0,48,2,47,2,47,3,46,3,46,3,28,-2,47,2,47,-4,19,4,19,-4,1,4,1,-3,0,-4,-1,-4,-1,-2,-1,3,0,4,-1,4,-1,2,-1,2,-1,2,0,-2,-1,-2,0,-4,19,-6,2,-6,2,-6,1,-6,1,-4,1,4,1,6,1,6,1,6,2,6,2,4,19,-3,29,3,29,-3,46,3,46,0,19,-1,1,0,19,1,1,
	};
	private double scale=0.125;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render target crosshair
		if(showtxy){
			int a=0, b=6;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslatef(c.$cx(tx), c.$cy(ty), 0);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			
			GL11.glVertex2d(-a, -a);
			GL11.glVertex2d(-b, -b);
		
			GL11.glVertex2d(+a, +a);
			GL11.glVertex2d(+b, +b);
		
			GL11.glVertex2d(-a, +a);
			GL11.glVertex2d(-b, +b);
		
			GL11.glVertex2d(+a, -a);
			GL11.glVertex2d(+b, -b);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			// deprecated
			/*
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslatef(c.$cx(tx), c.$cy(ty), 0);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			for(int i=0; i<lc.length; i+=4){
				GL11.glVertex2d(lc[i]*c.$zoom()*tscale, lc[i+1]*c.$zoom()*tscale);
				GL11.glVertex2d(lc[i+2]*c.$zoom()*tscale, lc[i+3]*c.$zoom()*tscale);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			//*/
		}

	}
	
	protected void onImpactWith(StardustEntity e) {
		deactivate();
	}

	public void onDeath() {
		game.$currentState().addEntity(new AnnihilatingExplosion(game, x, y, 16, owner));
	}

}
