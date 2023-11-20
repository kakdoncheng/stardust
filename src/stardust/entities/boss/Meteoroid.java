package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.AntiMatterProjectile;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.Vector;
import engine.gfx.Camera;

public class Meteoroid extends Projectile{

	private double[] vertices;
	public Meteoroid(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 180, 1800, owner);
		setBoundRadius(8);
		vertices=new double[16];
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(r*0.6, r*1.4);
		}
	}
	
	private double dtt=0;
	private boolean rebound=false;
	private boolean hitCeraphim=false;
	public void update(double dt) {
		super.update(dt);
		blip();
		dtt-=dt;
		if(dtt<=0){
			double tt=game.$prng().$double(0, 2*Math.PI);
			double di=game.$prng().$double(0, r*0.25);
			double dx=Vector.vectorToDx(tt, di);
			double dy=Vector.vectorToDy(tt, di);
			game.$currentState().addEntity(new TracerDot(game,x+dx,y+dy,game.$prng().$double(r*0.6, r*1.4),1,1,game.$prng().$int(4, 8)));
			dtt+=1.0/30;
		}
		t+=dt;
	}
	
	public void render(Camera c) {
		int seg=vertices.length;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		int vi=0;
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,vertices[vi]*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,vertices[vi]*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,r*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		if(range>0 && !hitCeraphim){
			active=true;
		}
	}

	protected void onImpactWith(StardustEntity e) {
		if(!rebound && e instanceof AntiMatterProjectile){
			rebound=true;
			setSpeedVector(e.directionTo(this), 240);
			game.$currentState().addEntity(new Explosion(game, x, y, 16));
		}
		if(rebound && e instanceof Ceraphim){
			hitCeraphim=true;
			game.$currentState().addEntity(new ElectromagneticPulse(game, x, y));
			return;
		}
		e.setTarget(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, x, y, 4));
	}
}
