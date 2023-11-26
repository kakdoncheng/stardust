package stardust.entities;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;
import stardust.StardustGame;

public class AntiMatterMissile extends Projectile{

	public AntiMatterMissile(StardustGame game, double t, StardustEntity owner) {
		super(game, owner.$x(), owner.$y(), t, 280, 640, owner);
		setBoundRadius(2);
		Audio.addSoundEffect("fire-blaster", 1);
		tdlx=x;
		tdly=y;
		dtt=280.0/60.0;
	}

	// bias system where projectiles will attempt to choose
	// the nearest targetable enemy to the point (x, y) 
	private boolean bias=false;
	private double bx=0;
	private double by=0;
	public void biasTowards(double x, double y){
		bx=x;
		by=y;
		bias=true;
	}
	
	// track last tracer dot
	private double tdlx;
	private double tdly;
	private double dtt;
	
	public void update(double dt) {
		super.update(dt);
		blip();
		
		// find new target
		if(target==null || !target.isActive()){
			StardustEntity te=null;
			for(StardustEntity e:game.$currentState().$entities()){
				if(e==owner||!e.isCollidable()||!e.isActive()||e instanceof Projectile){
					continue;
				}
				if(target==null && bias){
					if(te==null||Vector.distanceFromTo(bx, by, e.$x(), e.$y())<Vector.distanceFromTo(bx, by, te.$x(), te.$y())){
						te=e;
					}
				}else{
					if(te==null||distanceTo(e)<distanceTo(te)){
						te=e;
					}
				}
			}
			setTarget(te);
		}
		if(target!=null){
			rotateTowards(target, Math.PI*2, dt);
			setSpeedVector(t, $speed());
		}else{
			setDirection($speedt());
		}
		
		
		// tracer dot affected by bullet time
		// lock dot position by checking last dot
		//*
		double dld=Vector.distanceFromTo(tdlx, tdly, x, y);
		while(dld>=dtt) {
			double tld=Vector.directionFromTo(tdlx, tdly, x, y);
			double ddx=Vector.vectorToDx(tld, dtt);
			double ddy=Vector.vectorToDy(tld, dtt);
			tdlx+=ddx;
			tdly+=ddy;
			dld=Vector.distanceFromTo(tdlx, tdly, x, y);
			game.$currentState().addEntity(new TracerDot(game,tdlx,tdly,1,fade,1));
		}
		//*/
		
		// deprecated tracer dot code
		/*
		dtt-=dt;
		if(dtt<=0){
			game.$currentState().addEntity(new TracerDot(game,x,y,1,fade,1));
			dtt+=1.0/60;
		}
		//*/
		//t+=dt;
	}
	
	// x1, y1, x2, y2 line render
	private double l[]={
		//-1,1,0,6,0,6,1,1,1,1,1,0,1,0,0,-1,0,-1,-1,0,-1,0,-1,1,-1,0,-2,-2,-2,-2,0,-1,0,-1,2,-2,2,-2,1,0,
		-2,-2,0,8,0,8,2,-2,2,-2,1,0,1,0,-1,0,-1,0,-2,-2,
	};
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(fade);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//CharGraphics.drawString(String.format("%.1f %.1f %.1f %.1f",tdlx,tdly,x,y), c.$cx(x), c.$cy(y), 1f);
		
	}

	public void onDeath() {
		//game.$currentState().addEntity(new Explosion(game, x, y, 8));
	}

	protected void onImpactWith(StardustEntity e) {
		e.setKiller(owner);
		e.deactivate();
		game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*2));
	}

}
