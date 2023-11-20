package stardust.entities.silo;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;

public class Warhead extends Projectile{
	
	private static boolean ff=false;
	public static void setff(boolean a){
		ff=a;
	}
	public static boolean isFF(){
		return ff;
	}
	
	public Warhead(StardustGame game, double x, double y) {
		super(game, x, y, 0, 60, 12000, null);
		setBoundRadius(2);
		ox=x;
		oy=y;
	}
	
	private double dtt=0;
	private double ox;
	private double oy;
	public void update(double dt){
		//blip();
		
		if(ff){
			setSpeedVector(this.$speedt(), 240.0);
		}
		
		super.update(dt);
		dtt-=dt;
		if(dtt<=0){
			//game.$currentState().addEntity(new TracerLine(game,ox,oy,x,y,this));
			//ox=x;
			//oy=y;
			dtt+=1.0/30;
		}
	}

	// x1, y1, x2, y2 line render
	private double l[]={
			-2,-1,0,0,0,0,2,-1,2,-1,2,4,2,4,1,6,1,6,-1,6,-1,6,-2,4,-2,4,-2,-1,
	};
	private double scale=0.5;
	public void render(Camera c) {
		// render warhead
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
		
		// tracer line
		///*
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		GL11.glVertex2d(c.$cx(ox), c.$cy(oy));
		GL11.glVertex2d(c.$cx(x), c.$cy(y));
		//GL11.glVertex2d(c.$cx(x+Vector.vectorToDx(t+Math.PI, 30)), c.$cy(y+Vector.vectorToDy(t+Math.PI, 30)));
		GL11.glEnd();
		//*/
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

	public void onDeath() {
		game.$currentState().addEntity(new WarheadExplosion(game, x, y, 16, owner));
	}

	protected void onImpactWith(StardustEntity e) {
		if(e instanceof Warhead){
			return;
		}
		deactivate();
	}

}
