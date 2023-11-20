package stardust.entities.boss;

import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import stardust.entities.gradius.GradiusProjectile;

public class PossessedMachine extends StardustEntity{
	
	public PossessedMachine(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(25);
		setDirection(Math.PI/2);
		setSpeedVector(Math.PI/2, speed);
	}
	
	//private boolean tagged=false;
	private double speed=45;
	private double aimt=0;
	private double dm=0;
	private double ct=1;
	
	private double pointT=0;
	private void useBulletHail(double dt){
		pointT+=dt;
		if(pointT>=0.125){
			pointT-=0.125;
			game.$currentState().addEntity(new StardustProjectile(game, game.$prng().$double(0, 2*Math.PI), this));
		}
	}
	
	private double bhc=0;
	private void useBulletHalo(double dt){
		bhc-=dt;
		if(bhc<=0){
			// explod
			double ci=2*Math.PI;
			double cis=ci/128;
			for(double i=0;i<ci-cis;i+=cis){
				game.$currentState().addEntity(new GradiusProjectile(game, i, this));
			}
			bhc=0.5;
			Audio.addSoundEffect("fire-halo", 1);
		}
	}
	
	private double ebc=0;
	private void useEnergyBolt(double dt){
		ebc-=dt;
		if(ebc<=0){
			double ci=2*Math.PI;
			double cis=ci/8;
			for(double i=0;i<ci;i+=cis){
				StardustEntity e=new EnergyBolt(game, i, this);
				e.setTarget(target);
				game.$currentState().addEntity(e);
			}
			game.$currentState().addEntity(new Explosion(game, x, y, 12));
			ebc=1;
		}
	}
	
	private double ipt=0;
	private void useInfernoBolt(double dt){
		ipt-=dt;
		if(ipt<0){
			ipt=0.25;
			game.$currentState().addEntity(new Explosion(game,x,y,12));
			StardustEntity e=new InfernoBolt(game, aimt, this);
			e.applyAccelerationVector(aimt, -20, 1);
			game.$currentState().addEntity(e);
		}
	}
	
	private double hlct=0;
	private void useHeavyLascannon(double dt){
		hlct-=dt;
		if(hlct<0){
			hlct=0.05;
			StardustEntity e=new LascannonBeam(game, x-52, y, Math.PI/2, this);
			game.$currentState().addEntity(e);
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
		if(dm>=2.5){
			//if(!tagged){
			//	tagged=true;
				//game.$currentState().addEntity(new FlashingDestroyIndicator(game, this, true));
			//}
			// move up and down
			ct+=dt;
			if(ct%4<=2){
				setSpeedVector(Math.PI, speed*2);
			}else{
				setSpeedVector(0, speed*2);
			}
			// attack cycles
			if(ct%6<3){
				useEnergyBolt(dt);
			}else if(ct%6<3.5){
				useInfernoBolt(dt);
			}else if(ct%6<4){
				useHeavyLascannon(dt);
			}else if(ct%6<4.5){
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
		0,-6,3,-5,3,-5,5,-3,5,-3,6,0,6,0,5,3,5,3,3,5,3,5,0,6,0,6,-3,5,-3,5,-5,3,-5,3,-6,0,-6,0,-5,-3,-5,-3,-3,-5,-3,-5,0,-6,-8,0,-6,6,-6,6,-2,8,-2,8,-2,12,2,12,2,8,2,8,6,6,6,6,8,0,-2,12,-8,12,-8,12,-8,36,-8,36,-12,16,-12,16,-24,8,-24,8,-24,0,-24,0,-8,0,2,12,8,12,8,12,8,36,8,36,12,16,12,16,24,8,24,8,24,0,24,0,8,0,-4,9,-5,10,-5,10,-4,11,-4,11,-3,10,-3,10,-4,9,4,9,3,10,3,10,4,11,4,11,5,10,5,10,4,9,-7,13,-7,17,-7,17,7,17,7,17,7,13,7,13,-7,13,-8,14,-7,14,-8,16,-7,16,7,14,8,14,7,16,8,16,-9,1,-10,2,-10,2,-9,3,-9,3,-8,2,-8,2,-9,1,9,1,8,2,8,2,9,3,9,3,10,2,10,2,9,1,22,1,21,2,21,2,22,3,22,3,23,2,23,2,22,1,12,13,11,14,11,14,12,15,12,15,13,14,13,14,12,13,-22,1,-23,2,-23,2,-22,3,-22,3,-21,2,-21,2,-22,1,-12,13,-13,14,-13,14,-12,15,-12,15,-11,14,-11,14,-12,13,-3,18,-6,19,-6,19,-3,20,-3,20,-3,21,-3,21,-6,22,-6,22,-3,23,-3,23,-3,24,-3,24,-6,25,-6,25,-3,26,-3,26,-3,27,-3,27,-6,28,-6,28,-3,29,-3,29,3,29,3,29,6,28,6,28,3,27,3,27,3,26,3,26,6,25,6,25,3,24,3,24,3,23,3,23,6,22,6,22,3,21,3,21,3,20,3,20,6,19,6,19,3,18,3,18,3,17,-3,18,-3,17,-3,18,3,18,-3,20,3,20,-3,21,3,21,-3,23,3,23,3,24,-3,24,-3,26,3,26,-3,27,3,27,-2,9,0,8,0,8,2,9,-2,11,2,11,-3,13,-3,12,3,13,3,12,-3,13,-8,12,3,13,8,12,-8,-2,-20,-2,-20,-2,-20,-8,-20,-8,-10,-8,-10,-8,-10,-16,-10,-16,-5,-23,-5,-23,-4,-23,-4,-23,-4,-16,-4,-16,-3,-16,-3,-16,-3,-23,-3,-23,-2,-23,-2,-23,-2,-16,-2,-16,-1,-16,-1,-16,-1,-23,-1,-23,1,-23,1,-23,1,-16,1,-16,2,-16,2,-16,2,-23,2,-23,3,-23,3,-23,3,-16,3,-16,4,-16,4,-16,4,-23,4,-23,5,-23,5,-23,10,-16,10,-16,10,-8,10,-8,20,-8,20,-8,20,-2,20,-2,8,-2,8,-2,6,-6,6,-6,0,-8,0,-8,-6,-6,-6,-6,-8,-2,-18,-6,-19,-5,-19,-5,-18,-4,-18,-4,-17,-5,-17,-5,-18,-6,18,-6,17,-5,17,-5,18,-4,18,-4,19,-5,19,-5,18,-6,8,-15,7,-14,7,-14,8,-13,8,-13,9,-14,9,-14,8,-15,-8,-15,-9,-14,-9,-14,-8,-13,-8,-13,-7,-14,-7,-14,-8,-15,-10,-2,-10,0,10,0,10,-2,19,0,19,-2,-19,0,-19,-2,-18,-8,-22,-12,-10,-16,-14,-20,-14,-20,-22,-12,-18,-8,-10,-16,-17,-9,-16,-8,-11,-15,-10,-14,-22,-12,-22,-14,-22,-14,-16,-20,-16,-20,-14,-20,-16,-20,-22,-26,-22,-26,-28,-20,-28,-20,-22,-14,-28,-20,-28,-22,-28,-22,-24,-26,-24,-26,-22,-26,10,-16,18,-8,18,-8,22,-12,10,-16,14,-20,14,-20,22,-12,22,-12,22,-14,22,-14,16,-20,16,-20,14,-20,16,-20,22,-26,22,-26,28,-20,28,-20,22,-14,28,-20,28,-22,28,-22,24,-26,24,-26,22,-26,-12,-18,-7,-20,12,-18,7,-20,11,-15,10,-14,17,-9,16,-8,16,-8,10,-14,-10,-14,-16,-8,-8,-19,-6,-28,-6,-28,-5,-28,-5,-28,-5,-23,5,-23,5,-28,5,-28,6,-28,6,-28,8,-19,-4,-22,-3,-22,-2,-22,-1,-22,1,-22,2,-22,3,-22,4,-22,-3,30,-3,35,-3,35,3,35,3,35,3,30,3,30,-3,30,-3,30,-2,29,3,30,2,29,-18,12,-18,14,-18,14,-15,14,-18,14,-22,22,-22,22,-15,14,15,14,18,14,18,14,18,12,18,14,22,22,22,22,15,14,20,-7,31,-5,31,-5,31,2,31,2,24,4,-20,-7,-31,-5,-31,-5,-31,2,-31,2,-24,4,-31,-5,-32,-4,-32,-4,-32,1,-32,1,-31,2,31,-5,32,-4,32,-4,32,1,32,1,31,2,
	};
	private double scale=1.5;
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(90, 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void onDeath() {
		active=true;
	}

}