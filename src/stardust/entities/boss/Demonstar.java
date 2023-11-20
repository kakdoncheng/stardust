package stardust.entities.boss;

import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import stardust.entities.demonstar.MartianProjectile;

public class Demonstar extends StardustEntity{
	
	public Demonstar(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(25);
		setDirection(game.$prng().$double(0, 8)*(Math.PI/4));
		setSpeedVector(0, speed);
	}
	
	//private boolean tagged=false;
	private double speed=45;
	private double aimt=0;
	private double dm=0;
	private double ct=0;
	
	private double pointT=0;
	private void useBulletHail(double dt){
		pointT+=dt;
		if(pointT>=0.125){
			pointT-=0.125;
			game.$currentState().addEntity(new StardustProjectile(game, game.$prng().$double(0, 2*Math.PI), this));
		}
	}
	
	private double bhc=0;
	private boolean bhos=false;
	private void useBulletHalo(double dt){
		bhc-=dt;
		if(bhc<=0){
			// explod
			double ci=2*Math.PI;
			double cis=ci/32;
			for(double i=0;i<ci;i+=cis){
				game.$currentState().addEntity(new MartianProjectile(game, i+(bhos?cis/2:0), this));
			}
			bhc=0.1;
			bhos=!bhos;
			Audio.addSoundEffect("fire-halo", 1);
		}
	}
	
	private double lct=0;
	private boolean lcos=false;
	private void useTwinLinkedLascannons(double dt){
		lct-=dt;
		if(lct<=0){
			game.$currentState().addEntity(new LascannonBeam(game, this.x+(lcos?-8:8), this.y+42, 0, this));
			lct=0.25;
			lcos=!lcos;
		}
	}
	
	private double ipt=0;
	private boolean ios=false;
	private void useInfernoBolt(double dt){
		ipt-=dt;
		if(ipt<0){
			ipt=0.5;
			if(ios){
				aimt+=0.1;
			}else{
				aimt-=0.1;
			}
			game.$currentState().addEntity(new Explosion(game,x,y,12));
			game.$currentState().addEntity(new InfernoBolt(game, aimt-0.1, this));
			game.$currentState().addEntity(new InfernoBolt(game, aimt+0.1, this));
			StardustEntity e=new InfernoBolt(game, aimt, this);
			e.applyAccelerationVector(aimt, -20, 1);
			game.$currentState().addEntity(e);
			ios=!ios;
		}
	}
	
	private int health=100;
	private double expT=0;
	public int $health(){
		return health;
	}
	public void damage(){
		health--;
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
		if(dm>=2.25){
			//if(!tagged){
			//	tagged=true;
				//game.$currentState().addEntity(new FlashingDestroyIndicator(game, this, true));
			//}
			// move in diamond
			ct+=dt;
			if(ct%2<=0.5){
				setSpeedVector(Math.PI*0.25, speed*2);
			}else if(ct%2<=1){
				setSpeedVector(Math.PI*1.75, speed*2);
			}else if(ct%2<=1.5){
				setSpeedVector(Math.PI*1.25, speed*2);
			}else{
				setSpeedVector(Math.PI*0.75, speed*2);
			}
			// attack cycles
			if(ct%5.5<3){
				useInfernoBolt(dt);
			}else if(ct%5.5<3.75){
				useTwinLinkedLascannons(dt);
			}else if(ct%5.5<3.95){
				useBulletHalo(dt);
			}
			if(health<33){
				useBulletHail(dt);
			}
			
		}else{
			dm+=dt;
			health=100;
			
		}
		
		aimt=directionTo(target);
		
		updateBlip(dt);
		updatePosition(dt);
		wraparoundIfOutOfBounds();
	}
	
	private double l[]={
		-7,16,-6,20,-6,20,-4,20,-4,20,-3,16,3,16,4,20,4,20,6,20,6,20,7,16,
		-20,-8,-20,8,-20,8,20,8,20,8,20,-8,20,-8,10,-8,10,-8,10,-16,10,-16,9,-22,9,-22,8,-22,8,-22,8,-16,8,-16,7,-16,7,-16,7,-23,7,-23,6,-23,6,-23,6,-16,6,-16,5,-16,5,-16,5,-23,5,-23,4,-23,4,-23,4,-16,4,-16,3,-16,3,-16,3,-23,3,-23,2,-23,2,-23,2,-16,2,-16,1,-16,1,-16,1,-23,1,-23,-1,-23,-1,-23,-1,-16,-1,-16,-2,-16,-2,-16,-2,-23,-2,-23,-3,-23,-3,-23,-3,-16,-3,-16,-4,-16,-4,-16,-4,-23,-4,-23,-5,-23,-5,-23,-5,-16,-5,-16,-6,-16,-6,-16,-6,-23,-6,-23,-7,-23,-7,-23,-7,-16,-7,-16,-8,-16,-8,-16,-8,-22,-8,-22,-9,-22,-9,-22,-10,-16,-10,-16,-10,-8,-10,-8,-20,-8,-20,-8,-19,-10,-19,-10,-10,-10,20,-8,19,-10,19,-10,10,-10,-15,-10,-13,-16,-13,-16,-10,-16,10,-16,13,-16,13,-16,15,-10,13,-16,13,-30,13,-30,3,-30,3,-30,3,-23,-3,-23,-3,-30,-3,-30,-13,-30,-13,-30,-13,-16,-13,-30,-11,-32,-11,-32,-5,-32,-5,-32,-3,-30,3,-30,5,-32,5,-32,11,-32,11,-32,13,-30,-14,-13,-16,-22,-16,-22,-20,-20,-20,-20,-19,-10,14,-13,16,-22,16,-22,20,-20,20,-20,19,-10,0,-14,-3,-13,-3,-13,-5,-11,-5,-11,-6,-8,-6,-8,-5,-5,-5,-5,-3,-3,-3,-3,0,-2,0,-2,3,-3,3,-3,5,-5,5,-5,6,-8,6,-8,5,-11,5,-11,3,-13,3,-13,0,-14,-20,0,-12,0,-12,0,-11,7,-11,7,-9,7,-9,7,-9,3,-9,3,-7,3,-7,3,-7,7,-7,7,-5,7,-5,7,-5,3,-5,3,-3,3,-3,3,-3,7,-3,7,-1,7,-1,7,-1,3,-1,3,1,3,1,3,1,7,1,7,3,7,3,7,3,3,3,3,5,3,5,3,5,7,5,7,7,7,7,7,7,3,7,3,9,3,9,3,9,7,9,7,11,7,11,7,13,0,13,0,20,0,-18,-7,-19,-6,-19,-6,-18,-5,-18,-5,-17,-6,-17,-6,-18,-7,-18,5,-19,6,-19,6,-18,7,-18,7,-17,6,-17,6,-18,5,18,-7,17,-6,17,-6,18,-5,18,-5,19,-6,19,-6,18,-7,18,5,17,6,17,6,18,7,18,7,19,6,19,6,18,5,-8,-15,-9,-14,-9,-14,-8,-13,-8,-13,-7,-14,-7,-14,-8,-15,8,-15,7,-14,7,-14,8,-13,8,-13,9,-14,9,-14,8,-15,-20,8,-14,12,20,8,14,12,14,8,14,13,-14,8,-14,13,-14,13,-13,13,-13,13,-13,12,-13,12,13,12,13,12,13,13,13,13,14,13,-12,9,-13,10,-13,10,-12,11,-12,11,-11,10,-11,10,-12,9,12,9,11,10,11,10,12,11,12,11,13,10,13,10,12,9,20,-7,21,-7,21,-7,21,3,21,3,20,3,22,-6,23,-10,23,-10,24,-6,24,-6,24,2,24,2,23,6,23,6,22,2,22,2,22,-6,25,-6,26,-10,26,-10,27,-6,27,-6,27,2,27,2,26,6,26,6,25,2,25,2,25,-6,28,-6,29,-10,29,-10,30,-6,30,-6,30,2,30,2,29,6,29,6,28,2,28,2,28,-6,21,-6,22,-6,24,-6,25,-6,27,-6,28,-6,21,2,22,2,24,2,25,2,27,2,28,2,30,2,31,2,30,-6,31,-6,31,-6,31,2,-20,-7,-21,-7,-21,-7,-21,3,-21,3,-20,3,-22,-6,-23,-10,-23,-10,-24,-6,-25,-6,-26,-10,-26,-10,-27,-6,-28,-6,-29,-10,-29,-10,-30,-6,-22,2,-23,6,-23,6,-24,2,-25,2,-26,6,-26,6,-27,2,-28,2,-29,6,-29,6,-30,2,-30,2,-30,-6,-28,-6,-28,2,-27,-6,-27,2,-25,-6,-25,2,-24,-6,-24,2,-22,-6,-22,2,-30,-6,-31,-6,-31,-6,-31,2,-31,2,-30,2,-28,-6,-27,-6,-25,-6,-24,-6,-22,-6,-21,-6,-28,2,-27,2,-25,2,-24,2,-22,2,-21,2,31,-6,32,-10,32,-10,34,-10,34,-10,34,6,34,6,32,6,32,6,31,2,-31,-6,-32,-10,-32,-10,-34,-10,-34,-10,-34,6,-34,6,-32,6,-32,6,-31,2,20,3,25,10,25,10,25,16,25,16,14,12,-20,3,-25,10,-25,10,-25,16,-25,16,-14,12,-25,10,-27,11,-27,11,-27,20,-27,20,-25,16,25,10,27,11,27,11,27,20,27,20,25,16,-10,12,-8,16,-8,16,8,16,8,16,10,12,-6,16,-6,20,-6,20,-4,20,-4,20,-4,16,4,16,4,20,4,20,6,20,6,20,6,16,
		//0,16,8,16,8,16,10,12,0,12,13,12,13,12,13,13,13,13,14,13,14,13,14,8,0,8,20,8,20,8,20,-8,20,-8,10,-8,10,-8,10,-16,10,-16,9,-22,9,-22,8,-22,8,-22,8,-16,8,-16,7,-16,7,-16,7,-23,7,-23,6,-23,6,-23,6,-16,6,-16,5,-16,5,-16,5,-23,5,-23,4,-23,4,-23,4,-16,4,-16,3,-16,3,-16,3,-23,3,-23,2,-23,2,-23,2,-16,2,-16,1,-16,1,-16,1,-23,1,-23,0,-23,3,-23,3,-30,3,-30,13,-30,13,-30,13,-16,13,-16,10,-16,3,-30,5,-32,5,-32,11,-32,11,-32,13,-30,13,-16,15,-10,10,-10,19,-10,19,-10,20,-8,14,-13,16,-22,16,-22,20,-20,20,-20,19,-10,20,-7,21,-7,21,-7,21,3,21,3,20,3,21,-6,22,-6,21,2,22,2,24,2,25,2,27,2,28,2,30,2,31,2,24,-6,25,-6,27,-6,28,-6,30,-6,31,-6,22,-6,22,2,22,2,23,6,23,6,24,2,22,-6,23,-10,23,-10,24,-6,24,-6,24,2,25,2,25,-6,25,-6,26,-10,26,-10,27,-6,27,-6,27,2,27,2,26,6,26,6,25,2,28,-6,29,-10,29,-10,30,-6,30,-6,30,2,30,2,29,6,29,6,28,2,28,2,28,-6,31,-6,32,-10,32,-10,34,-10,34,-10,34,6,34,6,32,6,32,6,31,2,31,2,31,-6,0,-13,3,-12,3,-12,5,-10,5,-10,6,-7,6,-7,5,-4,5,-4,3,-2,3,-2,0,-1,0,3,1,3,1,3,1,7,1,7,3,7,3,7,3,3,3,3,5,3,5,3,5,7,5,7,7,7,7,7,7,3,7,3,9,3,9,3,9,7,9,7,11,7,11,7,13,0,13,0,20,0,8,-15,7,-14,7,-14,8,-13,8,-13,9,-14,9,-14,8,-15,18,-7,17,-6,17,-6,18,-5,18,-5,19,-6,19,-6,18,-7,18,5,17,6,17,6,18,7,18,7,19,6,19,6,18,5,12,9,11,10,11,10,12,11,12,11,13,10,13,10,12,9,20,8,14,12,20,3,25,10,25,10,25,16,25,16,14,12,25,10,27,11,27,11,27,20,27,20,25,16,6,16,6,17,6,17,7,20,7,20,7,24,7,24,3,24,3,24,3,20,3,20,4,17,4,17,4,16,
	};
	private double scale=1.5;
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y+10), 0);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
			// mirror render
			//GL11.glVertex2d(-l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			//GL11.glVertex2d(-l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void onDeath() {
		active=true;
	}

}
