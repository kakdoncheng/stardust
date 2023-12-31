package stardust.entities.boss;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Projectile;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.Vector;
import engine.gfx.Camera;

public class Sinistar extends StardustEntity{
	
	public Sinistar(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(40);
		setDirection(game.$prng().$double(0, 8)*(Math.PI/4));
	}
	
	//private boolean tagged=false;
	private double speed=160;
	private int health=100;
	private double expT=0;
	private double dtt=0;
	public int $health(){
		return health;
	}
	public void damage(){
		health--;
	}
	
	private double dirT=0;
	private void moveRandomly(double dt){
		dirT+=dt;
		if(dirT>0.5){
			dirT-=0.5;
			t+=game.$prng().$int(-1, 1)*(Math.PI/4);
			t=Vector.constrainTheta(t);
			setSpeedVector(t, speed);
		}
	}
	
	public void update(double dt){
		if(health<1){
			blip();
			expT+=dt;
			if(expT>0.075){
				expT-=0.075;
				double tt=game.$prng().$double(0, 2*Math.PI);
				double di=game.$prng().$double(0, r*2);
				double dx=Vector.vectorToDx(tt, di);
				double dy=Vector.vectorToDy(tt, di);
				int w=game.$prng().$int(4, 16);
				game.$currentState().addEntity(new Explosion(game, x+dx, y+dy, w));
			}
			return;			
		}
		
		// attack/movement cycle
		// follow target
		if(target!=null && target.isActive()){
			setSpeedVector(directionTo(target), speed);
		}else{
			moveRandomly(dt);
		}
		
		// invincible wheel
		for(StardustEntity e:game.$currentState().$entities()){
			if(e==this || !e.isCollidable()){
				continue;
			}
			if(distanceTo(e)<this.r+e.$r()/2){
				e.setKiller(this);
				e.deactivate();
				if(e instanceof Projectile){
					game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), 4));
				}else{
					game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()));
				}
			}
		}
		
		// burning eyes
		dtt-=dt;
		if(dtt<=0){
			StardustEntity e;
			double tt=game.$prng().$double(0, 2*Math.PI);
			double di=game.$prng().$double(0, 1);
			double dx=Vector.vectorToDx(tt, di);
			double dy=Vector.vectorToDy(tt, di);
			
			e=new TracerDot(game,x+dx+7,y+dy-5.5,game.$prng().$double(1, 2),this.alpha,3,game.$prng().$int(4, 8));
			e.applyAccelerationVector($speedt(), $speed()*1.1, 1);
			game.$currentState().addEntity(e);
			
			e=new TracerDot(game,x+dx-7,y+dy-5.5,game.$prng().$double(1, 2),this.alpha,3,game.$prng().$int(4, 8));
			e.applyAccelerationVector($speedt(), $speed()*1.1, 1);
			game.$currentState().addEntity(e);
			dtt+=1.0/60;
		}
		
		//updateBlip(dt);
		blip();
		updatePosition(dt);
		wraparoundIfOutOfBounds();
	}
	
	private double l[]={
			//0,-24,10,-22,10,-22,18,-14,18,-14,18,-10,18,-10,20,-6,20,-6,18,-5,18,-5,18,-1,18,-1,20,0,20,0,10,4,10,4,6,20,6,20,4,18,4,18,0,19,2,-5,2,-4,2,-4,4,-1,4,-1,8,-2,8,-2,14,-2,14,-2,16,-4,16,-4,16,-6,16,-6,13,-9,13,-9,14,-10,14,-10,12,-10,12,-10,2,-5,2,-5,4,-3,4,-3,8,-3,8,-3,12,-6,12,-6,8,-8,7,-7,8,-6,8,-6,7,-5,7,-5,6,-6,6,-6,7,-7,0,-2,2,2,2,2,1,1,1,1,0,3,1,0,0,1,8,3,6,12,6,12,6,4,6,4,8,3,4,6,5,7,5,7,4,15,4,15,3,14,3,14,3,7,3,7,4,6,0,7,1,8,1,8,1,15,1,15,0,16,10,-22,16,-22,16,-22,16,-16,0,15,1,14,3,13,4,12,5,8,3,7,8,14,14,3,0,-23,1,-22,1,-22,0,-21,16,-13,15,-12,15,-12,16,-11,16,-11,17,-12,17,-12,16,-13,16,-1,15,0,15,0,16,1,16,1,17,0,17,0,16,-1,5,16,4,17,4,17,5,18,5,18,6,17,6,17,5,16,0,21,0,35,0,35,2,34,0,21,2,22,2,20,2,36,2,36,8,36,8,36,8,20,8,20,2,20,8,20,10,18,10,18,10,34,10,34,8,34,2,36,0,38,4,36,0,44,0,39,1,40,1,40,0,41,20,-8,20,-2,20,-2,36,-2,36,-2,36,-8,36,-8,20,-8,20,-8,18,-10,18,-10,34,-10,34,-10,34,-8,22,-2,21,0,21,0,22,2,21,0,35,0,35,0,34,-2,35,0,34,2,20,2,36,2,36,2,36,8,36,8,20,8,20,8,20,2,20,8,18,10,18,10,34,10,34,10,34,8,36,-2,38,0,38,0,36,2,36,4,44,0,44,0,36,-4,40,-1,39,0,39,0,40,1,40,1,41,0,41,0,40,-1,8,-34,10,-34,10,-34,10,-22,2,-36,8,-36,8,-36,8,-23,2,-24,2,-36,2,-34,0,-35,0,-35,0,-24,0,-44,4,-36,2,-36,0,-38,0,-39,1,-40,1,-40,0,-41,5,-35,4,-34,4,-34,5,-33,5,-33,6,-34,6,-34,5,-35,34,-6,33,-5,33,-5,34,-4,34,-4,35,-5,35,-5,34,-6,22,-6,21,-5,21,-5,22,-4,22,-4,23,-5,23,-5,22,-6,22,4,21,5,21,5,22,6,22,6,23,5,23,5,22,4,34,4,33,5,33,5,34,6,34,6,35,5,35,5,34,4,5,21,4,22,4,22,5,23,5,23,6,22,6,22,5,21,5,33,4,34,4,34,5,35,5,35,6,34,6,34,5,33,29,19,19,29,19,29,10,32,29,19,32,10,10,20,16,16,16,16,20,10,10,18,8,16,8,16,9,19,18,10,16,8,16,8,19,9,22,26,28,28,28,28,26,22,28,28,24,24,23,22,22,23,22,23,23,24,23,24,24,23,24,23,23,22,17,16,16,17,16,17,17,18,17,18,18,17,18,17,17,16,13,18,14,14,14,14,18,13,14,14,16,16,27,17,17,27,17,27,10,29,27,17,29,10,10,23,18,18,18,18,23,10,29,-19,19,-29,19,-29,10,-32,29,-19,32,-10,23,-24,22,-23,22,-23,23,-22,23,-22,24,-23,24,-23,23,-24,10,-29,17,-27,17,-27,27,-17,29,-10,27,-17,17,-18,18,-17,18,-17,17,-16,17,-16,16,-17,16,-17,17,-18,18,-18,23,-10,18,-18,16,-19,20,-10,18,-13,24,-24,28,-28,28,-28,26,-22,28,-28,22,-26,24,-8,26,-6,26,-6,30,-6,30,-6,32,-8,24,8,26,6,26,6,30,6,30,6,32,8,8,24,6,26,6,26,6,30,
			//6,30,8,32,8,-32,6,-30,6,-30,6,-26,6,-26,8,-24,
			2,-5,2,-4,2,-4,4,-1,4,-1,4,0,4,0,8,-2,8,-2,12,-2,12,-2,16,-6,16,-6,13,-9,13,-9,14,-10,14,-10,12,-10,12,-10,2,-5,2,-5,4,-3,4,-3,8,-3,8,-3,12,-6,12,-6,8,-8,0,-2,2,2,2,2,1,1,1,1,0,3,1,0,0,1,0,7,1,8,1,8,1,15,1,15,0,16,0,15,1,14,3,13,4,12,3,7,5,8,4,6,5,7,5,7,4,15,4,15,3,14,3,14,3,7,3,7,4,6,6,4,8,3,8,3,6,12,6,12,6,4,0,19,4,18,4,18,6,20,6,20,10,4,10,4,20,0,20,0,18,-1,18,-1,18,-5,18,-5,20,-6,20,-6,18,-10,18,-10,18,-14,18,-14,10,-22,10,-22,0,-24,0,-23,1,-22,1,-22,0,-21,14,3,8,14,10,-22,16,-22,16,-22,16,-16,0,-24,0,-35,0,-35,2,-34,0,-38,2,-36,0,-41,1,-40,1,-40,0,-39,0,-44,4,-36,2,-24,2,-36,2,-36,8,-36,8,-36,8,-23,5,-35,4,-34,4,-34,5,-33,5,-33,6,-34,6,-34,5,-35,8,-32,6,-30,6,-30,6,-26,6,-26,8,-24,8,-34,10,-34,10,-22,10,-34,10,-32,19,-29,19,-29,29,-19,29,-19,32,-10,18,-10,34,-10,34,-10,34,-8,18,-10,20,-8,20,-8,20,-2,20,-2,36,-2,36,-2,36,-8,36,-8,20,-8,24,-8,26,-6,26,-6,30,-6,30,-6,32,-8,16,-13,15,-12,15,-12,16,-11,16,-11,17,-12,17,-12,16,-13,16,-1,15,0,15,0,16,1,16,1,17,0,17,0,16,-1,5,16,4,17,4,17,5,18,5,18,6,17,6,17,5,16,20,2,36,2,36,2,36,8,36,8,20,8,20,8,20,2,21,-5,22,-6,22,-6,23,-5,23,-5,22,-4,22,-4,21,-5,34,-6,33,-5,33,-5,34,-4,34,-4,35,-5,35,-5,34,-6,22,-2,21,0,21,0,35,0,35,0,34,-2,21,0,22,2,35,0,34,2,22,4,21,5,21,5,22,6,22,6,23,5,23,5,22,4,34,4,33,5,33,5,34,6,34,6,35,5,35,5,34,4,16,-19,18,-18,18,-18,23,-10,20,-10,18,-13,17,-18,18,-17,18,-17,17,-16,17,-16,16,-17,16,-17,17,-18,23,-24,22,-23,22,-23,23,-22,23,-22,24,-23,24,-23,23,-24,10,-29,17,-27,17,-27,27,-17,27,-17,29,-10,22,-26,28,-28,28,-28,26,-22,28,-28,24,-24,36,-2,38,0,38,0,36,2,36,4,44,0,44,0,36,-4,40,-1,39,0,39,0,40,1,40,1,41,0,41,0,40,-1,24,8,26,6,26,6,30,6,30,6,32,8,34,8,34,10,34,10,18,10,18,10,20,8,18,10,16,8,16,8,19,9,32,10,29,19,29,19,19,29,19,29,10,32,8,34,10,34,10,34,10,18,10,18,8,20,8,20,8,36,8,36,2,36,2,36,2,20,2,20,8,20,9,19,8,16,8,16,10,18,2,22,0,21,0,21,0,35,0,35,2,34,2,36,0,38,0,39,1,40,1,40,0,41,0,44,4,36,5,35,4,34,4,34,5,33,5,33,6,34,6,34,5,35,8,32,6,30,6,30,6,26,6,26,8,24,6,22,5,23,5,23,4,22,4,22,5,21,5,21,6,22,10,29,17,27,17,27,27,17,27,17,29,10,23,10,18,18,18,18,10,23,10,20,16,16,16,16,20,10,18,13,14,14,14,14,13,18,22,26,28,28,28,28,26,22,28,28,24,24,23,22,22,23,22,23,23,24,23,24,24,23,24,23,23,22,17,16,16,17,16,17,17,18,17,18,18,17,18,17,17,16,16,16,14,14,
			
	};
	private double scale=1;
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,1,0,1);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
			// mirror render
			GL11.glVertex2d(-l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(-l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public boolean isCollidable(){
		return false;
	}
	
	public void onDeath() {
		active=true;
	}

}
