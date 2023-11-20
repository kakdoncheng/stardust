package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;

public class Power extends StardustEntity{
	public Power(StardustGame game, double x, double y, StardustEntity target) {
		super(game);
		setXY(x, y);
		setBoundRadius(5);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setSpeedVector(t, speed);
		setTarget(target);
		blip();
	}
	
	private int speed=45;
	private double rt=0;
	protected double cd=0;
	protected int ammo=99;
	
	public boolean isEmpty(){
		return ammo<=0;
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.125){
			AntiMatterMissile e=new AntiMatterMissile(game, owner.$t(), owner);
			e.biasTowards(MouseHandler.$mx(), MouseHandler.$my());
			game.$currentState().addEntity(e);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
	public void useSecondary(StardustEntity owner){
		ammo=0;
		game.$currentState().addEntity(new AntiMatterBomb(game, owner.$t(), owner));
	}
	
	public void update(double dt) {
		//blip();
		updateBlip(dt);
		rt+=Math.PI*dt;
		
		if(target!=null && distanceTo(target)<120){
			rotateTowards(target, 3*Math.PI, dt);
			setSpeedVector(t, speed+(120-distanceTo(target)));
		}else{
			setSpeedVector(t, speed);
		}
		
		updatePosition(dt);
		//wraparoundIfOutOfScreenBounds();
		deactivateIfOutOfBounds();
	}
	
	// x1, y1, x2, y2 line render
	//private double l[]={
	//	0,-6,4,-4,4,-4,6,0,6,0,4,4,4,4,0,6,0,6,-4,4,-4,4,-6,0,-6,0,-4,-4,-4,-4,0,-6,0,-7,-4,-5,-4,-5,-4,-4,-4,-4,-5,-4,-5,-4,-7,0,-7,0,-5,4,-5,4,-4,4,-4,4,-4,5,-4,5,0,7,0,7,4,5,4,5,4,4,4,4,5,4,5,4,7,0,7,0,5,-4,5,-4,4,-4,4,-4,4,-5,4,-5,0,-7,-4,-5,-5,-5,-5,-5,-5,-4,-5,4,-5,5,-5,5,-4,5,4,5,5,5,5,5,5,4,5,-4,5,-5,5,-5,4,-5,
		//0,-6,4,-4,4,-4,6,0,6,0,4,4,4,4,0,6,0,6,-4,4,-4,4,-6,0,-6,0,-4,-4,-4,-4,0,-6,
	//};
	//private double scale=1;
	private double li[]={
		0,-2,2,0,2,0,1,0,1,0,1,2,1,2,-1,2,-1,2,-1,0,-1,0,-2,0,-2,0,0,-2,
	};
	private double scalei=2;
	public void render(Camera c) {
		
		// render hud indicator
		double hdx=160;
		if(target!=null && Vector.distanceFromTo(c.$dx(), c.$dy(), x, y)>hdx){//distanceTo(target)>hdx){
			//double tt=target.directionTo(this);
			//double tx=target.$x()+Vector.vectorToDx(tt, hdx);
			//double ty=target.$y()+Vector.vectorToDy(tt, hdx);
			double tt=Vector.directionFromTo(c.$dx(), c.$dy(), x, y);
			double tx=c.$zoom()*Vector.vectorToDx(tt, hdx);
			double ty=c.$zoom()*Vector.vectorToDy(tt, hdx);
			GL11.glColor4d(1, 0.5, 0, 1);
			GL11.glColor4d(1, 0.5, 0, alpha);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			//GL11.glTranslated(c.$cx(tx), c.$cy(ty), 0);
			GL11.glTranslated(tx, ty, 0);
			GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
			GL11.glBegin(GL11.GL_LINES);
			int seg=3;
			int ir=3;
			double ci=2*Math.PI;
			double cis=ci/seg;
			for(double i=0;i<ci;i+=cis){
				double dxyx1=Vector.vectorToDx(i,ir), 
						dxyy1=Vector.vectorToDy(i,ir), 
						dxyx2=Vector.vectorToDx(i+(ci/seg),ir),
						dxyy2=Vector.vectorToDy(i+(ci/seg),ir);
				GL11.glVertex2d(dxyx1*c.$zoom(), dxyy1*c.$zoom());
				GL11.glVertex2d(dxyx2*c.$zoom(), dxyy2*c.$zoom());
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			//return;
		}
		
		GL11.glColor4d(1, 0.5, 0, 1);
		//setRadarColor(1);
		
		// render body
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(rt), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		//for(int i=0; i<l.length; i+=4){
		//	GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
		//	GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		//}
		int seg=5;
		int ir=5;
		double ci=2*Math.PI;
		double cis=ci/seg;
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,ir), 
					dxyy1=Vector.vectorToDy(i,ir), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),ir),
					dxyy2=Vector.vectorToDy(i+(ci/seg),ir);
			GL11.glVertex2d(dxyx1*c.$zoom(), dxyy1*c.$zoom());
			GL11.glVertex2d(dxyx2*c.$zoom(), dxyy2*c.$zoom());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render icon
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
		GL11.glBegin(GL11.GL_LINES);
		for(int i=0; i<li.length; i+=4){
			GL11.glVertex2d(li[i]*c.$zoom()*scalei, li[i+1]*c.$zoom()*scalei);
			GL11.glVertex2d(li[i+2]*c.$zoom()*scalei, li[i+3]*c.$zoom()*scalei);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public boolean isCollidable(){
		return false;
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new RadarBlip(game,x,y));
	}
}
